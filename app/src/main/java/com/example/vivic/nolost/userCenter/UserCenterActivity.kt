package com.example.vivic.nolost.userCenter

import android.os.Bundle
import cn.bmob.v3.BmobUser
import com.example.vivic.nolost.GlideApp

import com.example.vivic.nolost.R
import com.example.vivic.nolost.activity.BaseActivity
import com.example.vivic.nolost.bean.MyUser
import com.example.vivic.nolost.commonUtil.BindEventBus
import kotlinx.android.synthetic.main.activity_user_center.*

class UserCenterActivity : BaseActivity() {

    companion object {
        val TAG = UserCenterActivity::class.java.simpleName
    }

    val currentUser: MyUser by lazy {
        BmobUser.getCurrentUser(MyUser::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_center)
        initView()
    }

    private fun initView() {
        GlideApp.with(this).load(currentUser.avatar).into(iv_user_center_avatar)
        tv_user_center_username.text = currentUser.username
    }
}
