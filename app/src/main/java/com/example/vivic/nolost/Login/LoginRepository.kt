package com.example.vivic.nolost.Login

import android.util.Log
import cn.bmob.v3.BmobQuery
import cn.bmob.v3.BmobUser
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.*
import cn.sharesdk.framework.Platform
import cn.sharesdk.framework.PlatformActionListener
import cn.sharesdk.framework.ShareSDK
import cn.sharesdk.sina.weibo.SinaWeibo
import com.example.vivic.nolost.bean.MyUser
import org.json.JSONObject
import java.util.*

object LoginRepository {
    private val TAG = javaClass.simpleName

    /**
     * myUser:username+password,注册成功自动调用登录
     */
    fun signByUser(myUser: MyUser, iLoginCallback: ILoginCallback<MyUser>) {
        myUser.signUp(object : SaveListener<MyUser>() {
            override fun done(p0: MyUser?, p1: BmobException?) {
                if (p1 == null) {
                    Log.d(TAG, "signByNamePassword success,Myuser:$p0")
                    loginByUser(myUser, iLoginCallback)
                } else {
                    Log.d(TAG, "signByNamePassword fail :$p1")
                    iLoginCallback.error(p1)
                }
            }
        })
    }

    /**
     * myUser:username+password
     */
    fun loginByUser(myUser: MyUser, iLoginCallback: ILoginCallback<MyUser>) {
        myUser.login(object : SaveListener<MyUser>() {
            override fun done(p0: MyUser?, p1: BmobException?) {
                if (p1 == null) {
                    Log.d(TAG, "loginbyUser success,Myuser:$p0")
                    iLoginCallback.success(p0)
                } else {
                    Log.d(TAG, "loginbyUser failed,BmobException:$p1")
                    iLoginCallback.error(p1)
                }
            }
        })
    }

    fun signOrLoginByUser(myUser: MyUser, iLoginCallback: ILoginCallback<MyUser>) {
    }

    fun loginByThird(bmobThirdUserAuth: BmobUser.BmobThirdUserAuth, thirdUser: MyUser, iLoginCallback: ILoginCallback<MyUser>) {
        BmobUser.loginWithAuthData(bmobThirdUserAuth, object : LogInListener<JSONObject>() {
            override fun done(p0: JSONObject?, p1: BmobException?) {
                if (p1 == null) {
                    Log.d(TAG, "loginByThird success,${bmobThirdUserAuth.snsType},JSONObject:$p0")
                    iLoginCallback.success(thirdUser)
                } else {
                    Log.d(TAG, "loginByThird failed,BmobException:$p1")
                    iLoginCallback.error(p1)
                }
            }
        })
    }

    /**
     * shareSdk第三方登录
     */
    fun loginByThird(platform: String, iLoginCallback: ILoginCallback<MyUser>) {
        val plat = ShareSDK.getPlatform(platform)
        if (!plat.isAuthValid) {
            plat.platformActionListener = object : PlatformActionListener {
                override fun onComplete(platform: Platform, i: Int, hashMap: HashMap<String, Any>) { //成功回调在子线程，ui修改要回到主线程
                    Log.d(TAG, "onComplete: ${platform.db.exportData()}")
                    val snsType = getNameThird(platform.name)
                    val acessToken = platform.db.token
                    val expiresIn = platform.db.expiresIn.toString()
                    val userId = platform.db.userId
                    val bmobThirdUserAuth = BmobUser.BmobThirdUserAuth(snsType, acessToken, expiresIn, userId)
                    val thirdUser = MyUser()
                    thirdUser.username = plat.db.userName
                    thirdUser.avatar = plat.db.userIcon
                    thirdUser.sex = plat.db.userGender
                    loginByThird(bmobThirdUserAuth, thirdUser, iLoginCallback)
                }

                override fun onError(platform: Platform, i: Int, throwable: Throwable) {
                    Log.d(TAG, "onError: code = $throwable")
                    iLoginCallback.error(throwable)
                }

                override fun onCancel(platform: Platform, i: Int) {
                    Log.d(TAG, "onCancel: ")
                }
            }
        }
        plat.SSOSetting(false)
        plat.showUser(null)
    }

    fun getNameThird(platform: String): String {
        return when (platform) {
            SinaWeibo.NAME -> "weibo"
            else -> {
                ""
            }
        }
    }


    fun changePassword(oldPassword: String, newPassword: String, iLoginCallback: ILoginCallback<MyUser>) {
        BmobUser.updateCurrentUserPassword(oldPassword, newPassword, object : UpdateListener() {
            override fun done(p0: BmobException?) {
                if (p0 == null) {
                    Log.d(TAG, "changePassword success")
                    iLoginCallback.success(null)
                } else {
                    Log.d(TAG, "changePassword fail,BmobException:$p0")
                    iLoginCallback.error(p0)
                }
            }
        })
    }

    fun getCurrentUser(iLoginCallback: ILoginCallback<MyUser>) {
        val user = BmobUser.getCurrentUser(MyUser::class.java)
        if (user == null) {
            Log.d(TAG, "getCurrentUser success,Myuser:$user")
            iLoginCallback.success(user)
        } else {
            Log.d(TAG, "getCurrentUser failed")
            iLoginCallback.error(null)
        }
    }

    /**
     * 更新只会向后台更新，本地还得查询一次,并将结果返回
     */
    fun updateUserByNewUser(newUser: MyUser, iLoginCallback: ILoginCallback<MyUser>) {
        val currentUser = BmobUser.getCurrentUser(MyUser::class.java)
        newUser.update(currentUser.objectId, object : UpdateListener() {
            override fun done(p0: BmobException?) {
                if (p0 == null) {
                    Log.d(TAG, "updateUserByObjectId success")
                    val query = BmobQuery<MyUser>()
                    query.getObject(currentUser.objectId, object : QueryListener<MyUser>() {
                        override fun done(p0: MyUser?, p1: BmobException?) {
                            if (p1 == null) {
                                Log.d(TAG, "BmobQuery success,user:$p0")
                                iLoginCallback.success(p0)
                            } else {
                                Log.d(TAG, "BmobQuery failed,exception:$p1")
                            }
                        }
                    })
                } else {
                    Log.d(TAG, "updateUserByObjectId failed,exception:$p0")
                    iLoginCallback.error(p0)
                }
            }
        })
    }

    fun queryByUser(query: BmobQuery<MyUser>, iLoginCallback: ILoginCallback<MutableList<MyUser>>) {
        query.findObjects(object : FindListener<MyUser>() {
            override fun done(p0: MutableList<MyUser>?, p1: BmobException?) {
                if (p1 == null) {
                    Log.d(TAG, "queryByUser success,user:$p0")
                    iLoginCallback.success(p0)
                } else {
                    Log.d(TAG, "queryByUser failed,exception:$p1")
                    iLoginCallback.error(p1)
                }
            }
        })
    }

    fun logOut() {
        Log.d(TAG, "logOut")
        BmobUser.logOut()
    }


}
