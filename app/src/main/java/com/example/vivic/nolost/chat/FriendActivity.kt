package com.example.vivic.nolost.chat

import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import cn.bmob.newim.BmobIM
import cn.bmob.newim.bean.BmobIMConversation
import cn.bmob.newim.event.MessageEvent
import cn.bmob.newim.listener.MessageListHandler
import com.example.vivic.nolost.activity.BaseActivity
import com.example.vivic.nolost.commonUtil.bottomDialog.CommonBottomDialog
import kotlinx.android.synthetic.main.activity_friend.*


class FriendActivity : BaseActivity(), MessageListHandler {


    private var conversationList: MutableList<BmobIMConversation>? = null
    private var adapter: FriendAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.vivic.nolost.R.layout.activity_friend)
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
        adapter?.setOnItemClickListener(object : FriendAdapter.OnItemClickListener {
            override fun onItemClick(view: View, itemData: BmobIMConversation?) {
                ChatActivity.startChatActivity(this@FriendActivity, itemData)
            }
        })
        adapter?.setOnItemLongClickListener(object : FriendAdapter.OnItemLongClickListener {
            override fun onItemLongClick(view: View, itemData: BmobIMConversation?, position: Int) {
                var menuDialog = CommonBottomDialog.Builder(this@FriendActivity)
                        .item("删除") {
                            BmobIM.getInstance().deleteConversation(itemData);
                            adapter?.deleteItem(position)
                        }
                        .build()
                menuDialog.show()
            }
        })
    }

    override fun onResume() {
        super.onResume()
        reflashFriendList()
        BmobIM.getInstance().addMessageListHandler(this)
    }

    override fun onPause() {
        super.onPause()
        BmobIM.getInstance().removeMessageListHandler(this)
    }

    private fun reflashFriendList() {
        adapter?.clearFriend()
        adapter?.addFriend(BmobIM.getInstance().loadAllConversation())
    }

    override fun onMessageReceive(p0: MutableList<MessageEvent>?) {
        reflashFriendList()
    }

}
