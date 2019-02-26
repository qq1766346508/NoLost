package com.example.vivic.nolost.chat

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.inputmethod.InputMethodManager
import cn.bmob.newim.BmobIM
import cn.bmob.newim.bean.BmobIMConversation
import cn.bmob.newim.bean.BmobIMMessage
import cn.bmob.newim.bean.BmobIMTextMessage
import cn.bmob.newim.bean.BmobIMUserInfo
import cn.bmob.newim.core.BmobIMClient
import cn.bmob.newim.event.MessageEvent
import cn.bmob.newim.listener.MessageListHandler
import cn.bmob.newim.listener.MessageSendListener
import cn.bmob.newim.listener.MessagesQueryListener
import cn.bmob.v3.BmobUser
import cn.bmob.v3.exception.BmobException
import com.example.vivic.nolost.activity.BaseActivity
import com.example.vivic.nolost.bean.MyUser
import kotlinx.android.synthetic.main.activity_chat.*


class ChatActivity : BaseActivity(), MessageListHandler {


    private var user: MyUser? = null
    private var conversationEntrance: BmobIMConversation? = null //会话入口
    private var messageManager: BmobIMConversation? = null //消息管理
    private var targetIMUserInfo: BmobIMUserInfo? = null
    private var targetConversation: BmobIMConversation? = null //好友页跳转
    private var chatMessageAdapter: ChatMessageAdapter? = null
    private val inputMethodManager: InputMethodManager by lazy {
        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    }


    companion object {
        val TAG = ChatActivity::class.simpleName
        val TARGET_USER = "target_user"
        val TARGET_CONVERSATION = "target_conversation"

        fun startChatActivity(activity: Activity, bmobIMUserInfo: BmobIMUserInfo) {
            val intent = Intent(activity, ChatActivity::class.java).apply {
                this.putExtra(TARGET_USER, bmobIMUserInfo)
            }
            activity.startActivity(intent)
        }

        fun startChatActivity(activity: Activity, bmobIMConversation: BmobIMConversation?) {
            val intent = Intent(activity, ChatActivity::class.java).apply {
                this.putExtra(TARGET_CONVERSATION, bmobIMConversation)
            }
            activity.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.vivic.nolost.R.layout.activity_chat)
        user = BmobUser.getCurrentUser(MyUser::class.java)
        initChat()
        initView()
        loadHistory()
    }

    private fun loadHistory() {
        messageManager?.queryMessages(BmobIMMessage(), 200, object : MessagesQueryListener() {
            override fun done(list: MutableList<BmobIMMessage>?, bmobException: BmobException?) {
                if (bmobException == null) {
                    list?.let {
                        chatMessageAdapter?.addChatMessage(getChatMessageList2(it))
                        rv_chat_container.scrollToPosition(chatMessageAdapter?.messageList?.size!! - 1)
                        Log.d(TAG, "queryMessages success ;")
                    }
                } else {
                    Log.d(TAG, "queryMessages fail ;$bmobException")
                }
            }
        })
    }

    private fun initView() {
        iv_chat_back.setOnClickListener {
            finish()
        }
        rv_chat_container.setOnTouchListener { v, event ->
            inputMethodManager.hideSoftInputFromWindow(ed_chat_message!!.windowToken, 0)
            false
        }
        btn_chat_send.setOnClickListener {
            sendMessage()
        }
        tv_chat_target.text = conversationEntrance?.conversationTitle
        chatMessageAdapter = ChatMessageAdapter(null, conversationEntrance?.conversationIcon)
        rv_chat_container.adapter = chatMessageAdapter
        rv_chat_container.layoutManager = LinearLayoutManager(this)
        rv_chat_container.scrollToPosition(chatMessageAdapter?.messageList?.size!! - 1)
    }

    private fun initChat() {
        targetIMUserInfo = intent.getSerializableExtra(TARGET_USER) as? BmobIMUserInfo
        targetConversation = intent.getSerializableExtra(TARGET_CONVERSATION) as? BmobIMConversation
        targetIMUserInfo?.let {
            conversationEntrance = BmobIM.getInstance().startPrivateConversation(targetIMUserInfo, null);
        }
        targetConversation?.let {
            conversationEntrance = it
        }
        messageManager = BmobIMConversation.obtain(BmobIMClient.getInstance(), conversationEntrance);
    }

    private fun sendMessage() {
        val text = ed_chat_message.text.toString()
        val msg = BmobIMTextMessage()
        msg.content = text
        Log.d(TAG, "sendMessage:$text")
        messageManager?.sendMessage(msg, object : MessageSendListener() {
            override fun done(p0: BmobIMMessage?, bmobException: BmobException?) {
                if (bmobException == null) {
                    Log.d(TAG, "sendMessage success")
                    ed_chat_message.setText("")
                    chatMessageAdapter?.addChatMessage(mutableListOf(ChatMessage(ChatMessage.TYPE_SEND, text)))
                    rv_chat_container.scrollToPosition(chatMessageAdapter?.messageList?.size!! - 1)
                } else {
                    Log.d(TAG, "sendMessage fail -$bmobException")
                }
            }
        })
    }

    override fun onMessageReceive(messageList: MutableList<MessageEvent>?) {
        Log.d(TAG, "onMessageReceive:$messageList")
        messageList?.let {
            if (!rv_chat_container.canScrollVertically(1)) {
                chatMessageAdapter?.addChatMessage(getChatMessageList(it))
                rv_chat_container.scrollToPosition(chatMessageAdapter?.messageList?.size!! - 1)
            } else {
                chatMessageAdapter?.addChatMessage(getChatMessageList(it))
            }
        }
    }

    private fun getChatMessageList(messageList: MutableList<MessageEvent>): MutableList<ChatMessage> {
        val chatMessageList = mutableListOf<ChatMessage>()
        messageList.forEach {
            chatMessageList.add(ChatMessage(ChatMessage.TYPE_RECEIVE, it.message.content))
        }
        return chatMessageList
    }

    private fun getChatMessageList2(messageList: MutableList<BmobIMMessage>): MutableList<ChatMessage> {
        val chatMessageList = mutableListOf<ChatMessage>()
        messageList.forEach {
            if (it.fromId == BmobUser.getCurrentUser(MyUser::class.java).objectId) {
                chatMessageList.add(ChatMessage(ChatMessage.TYPE_SEND, it.content))
            } else {
                chatMessageList.add(ChatMessage(ChatMessage.TYPE_RECEIVE, it.content))
            }
        }
        return chatMessageList
    }

    override fun onResume() {
        super.onResume()
        BmobIM.getInstance().addMessageListHandler(this)
    }

    override fun onPause() {
        super.onPause()
        BmobIM.getInstance().removeMessageListHandler(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        messageManager?.updateLocalCache()
    }
}
