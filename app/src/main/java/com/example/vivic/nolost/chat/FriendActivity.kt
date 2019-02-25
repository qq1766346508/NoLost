package com.example.vivic.nolost.chat

import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import cn.bmob.newim.BmobIM
import cn.bmob.newim.bean.BmobIMConversation
import com.example.vivic.nolost.R
import com.example.vivic.nolost.activity.BaseActivity
import kotlinx.android.synthetic.main.activity_friend.*

class FriendActivity : BaseActivity() {
    private var conversationList: MutableList<BmobIMConversation>? = null
    private var adapter: FriendAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friend)
        conversationList = BmobIM.getInstance().loadAllConversation()
        initView()
    }

    private fun initView() {
        iv_friend_back.setOnClickListener {
            finish()
        }
        adapter = FriendAdapter()
        rv_friend.adapter = adapter
        rv_friend.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        rv_friend.layoutManager = LinearLayoutManager(this)
        adapter?.addFriend(conversationList)
    }
}
