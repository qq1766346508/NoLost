package com.example.vivic.nolost.Login

import android.util.Log
import cn.bmob.v3.BmobQuery
import cn.bmob.v3.BmobUser
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.FindListener
import cn.bmob.v3.listener.LogInListener
import cn.bmob.v3.listener.SaveListener
import cn.bmob.v3.listener.UpdateListener
import com.example.vivic.nolost.bean.MyUser
import org.json.JSONObject

object LoginRepository {
    private val TAG = javaClass.simpleName


    fun signByUser(myUser: MyUser, iLoginCallback: ILoginCallback<MyUser>) {
        myUser.signUp(object : SaveListener<MyUser>() {
            override fun done(p0: MyUser?, p1: BmobException?) {
                if (p1 == null) {
                    Log.d(TAG, "registerByNamePassword success,Myuser:$p0")
                    iLoginCallback.success(p0)
                } else {
                    iLoginCallback.error(p1)
                }
            }
        })
    }

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

    fun loginByThird(bmobThirdUserAuth: BmobUser.BmobThirdUserAuth, iLoginCallback: ILoginCallback<MyUser>) {
        BmobUser.loginWithAuthData(bmobThirdUserAuth, object : LogInListener<JSONObject>() {
            override fun done(p0: JSONObject?, p1: BmobException?) {
                if (p1 == null) {
                    Log.d(TAG, "loginByThird success,JSONObject:$p0")
                    iLoginCallback.success(null)
                } else {
                    Log.d(TAG, "loginByThird failed,BmobException:$p1")
                    iLoginCallback.error(p1)
                }
            }
        })
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

    fun updateUserByNewUser(newUser: MyUser, iLoginCallback: ILoginCallback<MyUser>) {
        val currentUser = BmobUser.getCurrentUser(MyUser::class.java)
        newUser.update(currentUser.objectId, object : UpdateListener() {
            override fun done(p0: BmobException?) {
                if (p0 == null) {
                    Log.d(TAG, "updateUserByObjectId success")
                    iLoginCallback.success(null)
                } else {
                    Log.d(TAG, "updateUserByObjectId failed,exception:$p0")
                    iLoginCallback.error(null)
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
