package com.example.vivic.nolost.lost.activity

import android.os.Bundle
import cn.bmob.v3.BmobUser
import com.example.vivic.nolost.activity.BaseActivity
import com.example.vivic.nolost.bean.MyUser


class ChatActivity : BaseActivity() {

    private var user: MyUser? = null

    companion object {
        val TAG = ChatActivity::class.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.vivic.nolost.R.layout.activity_chat)
        user = BmobUser.getCurrentUser(MyUser::class.java)

    }
}
