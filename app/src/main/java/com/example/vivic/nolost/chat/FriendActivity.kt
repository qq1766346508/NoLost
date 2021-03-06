package com.example.vivic.nolost.chat

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import cn.bmob.newim.BmobIM
import cn.bmob.newim.bean.BmobIMConversation
import cn.bmob.newim.event.MessageEvent
import cn.bmob.newim.listener.MessageListHandler
import com.example.vivic.nolost.activity.BaseActivity
import com.example.vivic.nolost.bean.MyUser
import com.example.vivic.nolost.bmob.ChatRepository
import com.example.vivic.nolost.bmob.IBmobCallback
import com.example.vivic.nolost.bmob.UserRepository
import com.example.vivic.nolost.commonUtil.bottomDialog.CommonBottomDialog
import kotlinx.android.synthetic.main.activity_friend.*


/**
 * 聊天列表
 */
class FriendActivity : BaseActivity(), MessageListHandler {


    private var adapter: FriendAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.vivic.nolost.R.layout.activity_friend)
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
        val list = BmobIM.getInstance().loadAllConversation()
        list.forEach {
            addSubscribe(UserRepository.queryUserByObject(it.conversationId, object : IBmobCallback<MyUser> {
                override fun success(user: MyUser?) {
                    ChatRepository.updateUserInfo(user!!.objectId, user.username, user.avatar)
                    it.conversationIcon = user.avatar
                    it.conversationTitle = user.username
                    BmobIM.getInstance().updateConversation(it)
                }

                override fun error(throwable: Throwable?) {

                }
            }))
        }


        adapter?.addFriend(list)
    }

    override fun onMessageReceive(p0: MutableList<MessageEvent>?) {
        //先让数据库用户更新了，该页面再更新
        Handler(Looper.getMainLooper()).postDelayed(
                {
                    reflashFriendList()
                }
                , 1000
        )
    }

}
