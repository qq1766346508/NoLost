package com.example.vivic.nolost.bmob

import android.util.Log
import cn.bmob.v3.BmobQuery
import cn.bmob.v3.BmobUser
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.*
import cn.sharesdk.framework.Platform
import cn.sharesdk.framework.PlatformActionListener
import cn.sharesdk.framework.ShareSDK
import cn.sharesdk.sina.weibo.SinaWeibo
import cn.sharesdk.tencent.qq.QQ
import com.example.vivic.nolost.bean.MyUser
import com.example.vivic.nolost.commonUtil.pref.CommonPref
import io.reactivex.disposables.Disposable
import org.json.JSONObject
import java.util.*

object UserRepository {
    private val TAG = javaClass.simpleName
    public val LAST_PLATFORM = "LAST_PLATFORM"

    /**
     * myUser:username+password,注册成功自动调用登录
     */
    fun signByUser(myUser: MyUser, iBmobCallback: IBmobCallback<MyUser>): Disposable {
        var disposable: Disposable? = null
        return myUser.signUp(object : SaveListener<MyUser>() {
            override fun done(p0: MyUser?, p1: BmobException?) {
                if (p1 == null) {
                    Log.d(TAG, "signByNamePassword success,Myuser:$p0")
                    disposable = loginByUser(myUser, iBmobCallback)
                } else {
                    Log.d(TAG, "signByNamePassword fail :$p1")
                    iBmobCallback.error(p1)
                    disposable?.dispose()
                }
            }
        })
    }

    /**
     * myUser:username+password
     */
    fun loginByUser(myUser: MyUser, iBmobCallback: IBmobCallback<MyUser>): Disposable {
        return myUser.login(object : SaveListener<MyUser>() {
            override fun done(p0: MyUser?, p1: BmobException?) {
                if (p1 == null) {
                    Log.d(TAG, "loginbyUser success,Myuser:$p0")
                    iBmobCallback.success(p0)
                } else {
                    Log.d(TAG, "loginbyUser failed,BmobException:$p1")
                    iBmobCallback.error(p1)
                }
            }
        })
    }

    fun signOrLoginByUser(myUser: MyUser, iBmobCallback: IBmobCallback<MyUser>) {
    }

    /**
     * Bmob的第三方登录接口
     */
    fun loginByThird(bmobThirdUserAuth: BmobUser.BmobThirdUserAuth, thirdUser: MyUser, iBmobCallback: IBmobCallback<MyUser>): Disposable {
        return BmobUser.loginWithAuthData(bmobThirdUserAuth, object : LogInListener<JSONObject>() {
            override fun done(p0: JSONObject?, p1: BmobException?) {
                if (p1 == null) {
                    Log.d(TAG, "loginByThird success,${bmobThirdUserAuth.snsType},JSONObject:$p0")
                    iBmobCallback.success(thirdUser)
                } else {
                    Log.d(TAG, "loginByThird failed,BmobException:$p1")
                    iBmobCallback.error(p1)
                }
            }
        })
    }

    /**
     * shareSdk第三方登录
     */
    fun loginByShareSdk(platform: String, iBmobCallback: IBmobCallback<MyUser>) {
        var disposable: Disposable? = null
        val plat = ShareSDK.getPlatform(platform)
        if (plat.isAuthValid) {
            plat.removeAccount(true)
        }
        plat.platformActionListener = object : PlatformActionListener {
            override fun onComplete(platform: Platform, i: Int, hashMap: HashMap<String, Any>) { //成功回调在子线程，ui修改要回到主线程
                Log.d(TAG, "onComplete: ${platform.db.exportData()}")
                val snsType = getNameThird(platform.name)
                val accessToken = platform.db.token
                val expiresIn = platform.db.expiresIn.toString()
                val userId = platform.db.userId
                Log.i(TAG, "snsType:$snsType,accessToken:$accessToken,expiresIn:$expiresIn,userId:$userId")
                val bmobThirdUserAuth = BmobUser.BmobThirdUserAuth(snsType, accessToken, expiresIn, userId)
                val thirdUser = MyUser().apply {
                    this.platform = snsType
                    this.username = plat.db.userName
                    this.avatar = plat.db.userIcon
                    this.gender = plat.db.userGender
                    this.background = hashMap.get("cover_image_phone").toString()
                }
                disposable = loginByThird(bmobThirdUserAuth, thirdUser, iBmobCallback)
//                val ite = hashMap.entries.iterator()
//                while (ite.hasNext()) {
//                    val entry = ite.next() as Map.Entry<*, *>
//                    val key = entry.key
//                    val value = entry.value
//                    Log.d(TAG, "key : $key -value : $value")
//                }
            }

            override fun onError(platform: Platform, i: Int, throwable: Throwable) {
                Log.d(TAG, "onError: code = $throwable")
                iBmobCallback.error(throwable)
                disposable?.isDisposed
            }

            override fun onCancel(platform: Platform, i: Int) {
                Log.d(TAG, "onCancel: ")
                iBmobCallback.error(null)
                disposable?.isDisposed
            }
        }
        plat.SSOSetting(false)
        plat.showUser(null)
    }

    fun getNameThird(platform: String): String {
        return when (platform) {
            SinaWeibo.NAME -> "weibo"
            QQ.NAME -> "qq"
            else -> {
                ""
            }
        }
    }


    fun changePassword(oldPassword: String, newPassword: String, iBmobCallback: IBmobCallback<MyUser>) {
        BmobUser.updateCurrentUserPassword(oldPassword, newPassword, object : UpdateListener() {
            override fun done(p0: BmobException?) {
                if (p0 == null) {
                    Log.d(TAG, "changePassword success")
                    iBmobCallback.success(null)
                } else {
                    Log.d(TAG, "changePassword fail,BmobException:$p0")
                    iBmobCallback.error(p0)
                }
            }
        })
    }


    /**
     * 更新只会向后台更新，本地还得查询一次,并将结果返回
     */
    fun updateUserByNewUser(newUser: MyUser, iBmobCallback: IBmobCallback<MyUser>?): Disposable {
        var disposable: Disposable? = null
        val currentUser = BmobUser.getCurrentUser(MyUser::class.java)
        disposable = newUser.update(currentUser.objectId, object : UpdateListener() {
            override fun done(p0: BmobException?) {
                if (p0 == null) {
                    Log.d(TAG, "updateUserByObjectId success")
                    val query = BmobQuery<MyUser>()
                    disposable = query.getObject(currentUser.objectId, object : QueryListener<MyUser>() {
                        override fun done(p0: MyUser?, p1: BmobException?) {
                            if (p1 == null) {
                                Log.d(TAG, "BmobQuery success,user:$p0")
                                iBmobCallback?.success(p0)
                            } else {
                                Log.d(TAG, "BmobQuery failed,exception:$p1")
                            }
                        }
                    })
                } else {
                    Log.d(TAG, "updateUserByObjectId failed,exception:$p0")
                    iBmobCallback?.error(p0)
                }
            }
        })
        return disposable!!
    }

    /**
     * 控制台修改数据，用户重开APP会拉取信息，并更新本地
     */
    fun fetchUserInfo() {
        BmobUser.fetchUserInfo(object : FetchUserInfoListener<BmobUser>() {
            override fun done(p0: BmobUser?, p1: BmobException?) {
                if (p1 == null) {
                    Log.d(TAG, "fetchUserInfo fail ;BmobException :$p1")
                } else {
                    Log.i(TAG, "fetchUserInfo success,user:$p0")
                }
            }
        })
    }

    fun queryByUser(query: BmobQuery<MyUser>, iBmobCallback: IBmobCallback<MutableList<MyUser>>) {
        query.findObjects(object : FindListener<MyUser>() {
            override fun done(p0: MutableList<MyUser>?, p1: BmobException?) {
                if (p1 == null) {
                    Log.d(TAG, "queryByUser success,user:$p0")
                    iBmobCallback.success(p0)
                } else {
                    Log.d(TAG, "queryByUser failed,exception:$p1")
                    iBmobCallback.error(p1)
                }
            }
        })
    }

    fun queryUserByObject(objectId: String, iBmobCallback: IBmobCallback<MyUser>) {
        val query = BmobQuery<MyUser>()
        query.getObject(objectId, object : QueryListener<MyUser>() {
            override fun done(p0: MyUser?, p1: BmobException?) {
                if (p1 == null) {
                    Log.d(TAG, "queryByUser success,user:$p0")
                    iBmobCallback.success(p0)
                } else {
                    Log.d(TAG, "queryByUser failed,exception:$p1")
                    iBmobCallback.error(p1)
                }
            }
        })
    }

    fun logOut() {
        val currentUser = BmobUser.getCurrentUser(MyUser::class.java)
        if (currentUser == null) {
            Log.d(TAG, "currentUser == null")
        } else {
            Log.i(TAG, "currentUser is $currentUser")
            BmobUser.logOut()
            Log.d(TAG, "logOut")
        }
        val platform = CommonPref.instance()?.getString(LAST_PLATFORM)
        val plat = ShareSDK.getPlatform(platform)
        plat?.removeAccount(true)
    }


}
