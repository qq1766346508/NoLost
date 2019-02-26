package com.example.vivic.nolost.lost.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import cn.bmob.newim.bean.BmobIMUserInfo
import cn.bmob.v3.BmobQuery
import cn.bmob.v3.BmobUser
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.QueryListener
import com.example.vivic.nolost.GlideApp
import com.example.vivic.nolost.R
import com.example.vivic.nolost.activity.BaseActivity
import com.example.vivic.nolost.bean.MyUser
import com.example.vivic.nolost.bmob.ChatRepository
import com.example.vivic.nolost.chat.ChatActivity
import com.example.vivic.nolost.login.LoginActivity
import com.example.vivic.nolost.lost.fragment.LoadMode
import com.example.vivic.nolost.lost.fragment.LostFragment
import com.example.vivic.nolost.userCenter.UserCenterActivity
import kotlinx.android.synthetic.main.activity_history.*

class HistoryActivity : BaseActivity() {

    private var creatorObjectId: String? = null;
    private var queryUser: MyUser? = null
    private var currentUser: MyUser? = null

    companion object {
        fun getActivity(activity: Activity, creatorObjectId: String?) {
            val intent = Intent(activity, HistoryActivity::class.java).apply {
                this.putExtra("creatorObjectId", creatorObjectId)
            }
            activity.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
        creatorObjectId = intent.getStringExtra("creatorObjectId")
        currentUser = BmobUser.getCurrentUser(MyUser::class.java)
        initView()
    }


    private fun initView() {
        supportFragmentManager.beginTransaction().replace(R.id.fl_history_container, LostFragment.newInstance(LoadMode.LOAD_MODE_HISTORY, creatorObjectId)).commitAllowingStateLoss()
        iv_history_back.setOnClickListener { finish() }
        val query = BmobQuery<MyUser>()
        query.getObject(creatorObjectId, object : QueryListener<MyUser>() {
            override fun done(p0: MyUser?, p1: BmobException?) {
                if (p1 == null) {
                    queryUser = p0
                    GlideApp.with(this@HistoryActivity).asDrawable().thumbnail(0.1f).load(queryUser?.avatar).override(iv_history_avatar.width).centerCrop().circleCrop().into(iv_history_avatar)
                    GlideApp.with(this@HistoryActivity).asDrawable().thumbnail(0.1f).load(queryUser?.background
                            ?: R.drawable.noicon120).override(iv_history_bg.width).centerCrop().into(iv_history_bg)
                    tv_history_username.text = queryUser?.username
                    updateuserInfo()
                }
            }
        })
        iv_history_avatar.setOnClickListener {
            UserCenterActivity.goToActivity(this@HistoryActivity, queryUser?.objectId)
        }
        tv_history_chat.setOnClickListener {
            if (currentUser == null) {
                startActivity(Intent(this@HistoryActivity, LoginActivity::class.java))
            } else {
                ChatActivity.startChatActivity(this@HistoryActivity, BmobIMUserInfo(queryUser?.objectId, queryUser?.username, queryUser?.avatar))
            }
        }
    }

    private fun updateuserInfo() {
        ChatRepository.updateUserInfo(queryUser?.objectId, queryUser?.username, queryUser?.avatar)
        if (currentUser == null || (currentUser != null && creatorObjectId != currentUser?.objectId))
            tv_history_chat.visibility = View.VISIBLE
    }
}
