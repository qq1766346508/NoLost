package com.example.vivic.nolost.bmob

import android.util.Log
import cn.bmob.newim.BmobIM
import cn.bmob.newim.bean.BmobIMUserInfo
import cn.bmob.newim.listener.ConnectListener
import cn.bmob.v3.exception.BmobException
import com.example.vivic.nolost.chat.ChatActivity

object ChatRepository {
    val TAG = ChatRepository::class.java.simpleName

    /**
     * 更新本地用户信息
     */
    fun updateUserInfo(userId: String?, name: String?, avatar: String?) {
        Log.d(TAG, "updateUserInfo,userId: $userId,name:$name,avatar:$avatar ")
        BmobIM.getInstance().updateUserInfo(BmobIMUserInfo().apply {
            this.userId = userId
            this.name = name
            this.avatar = avatar
        })
    }


    /**
     * 服务器连接
     */
    fun connect(userId: String, iBmobCallback: IBmobCallback<String>?) {
        Log.d(TAG, "connect,userId: $userId")
        BmobIM.connect(userId, object : ConnectListener() {
            override fun done(uid: String?, bmobException: BmobException?) {
                if (bmobException == null) {
                    Log.d(ChatActivity.TAG, "BmobIM.connect success,uid - $uid")
                    iBmobCallback?.success(uid)
                } else {
                    Log.d(ChatActivity.TAG, "BmobIM.connect fail,bmobException - $bmobException")
                    iBmobCallback?.error(bmobException)
                }
            }
        })
    }

    fun disConnect() {
        BmobIM.getInstance().disConnect();
    }
}