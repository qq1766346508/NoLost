package com.example.vivic.nolost.chat

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import cn.bmob.newim.bean.BmobIMUserInfo
import cn.bmob.v3.BmobUser
import com.example.vivic.nolost.GlideApp
import com.example.vivic.nolost.R
import com.example.vivic.nolost.bean.MyUser

class ChatMessageAdapter(messageList: MutableList<ChatMessage>?, targetAvatar: String?) : RecyclerView.Adapter<ChatMessageAdapter.ChatMessageHolder>() {

    var messageList: MutableList<ChatMessage>? = null
    private var targetAvatar: String? = null
    private var context: Context? = null

    init {
        if (messageList != null) {
            this.messageList = messageList
        } else {
            this.messageList = mutableListOf()
        }
        this.targetAvatar = targetAvatar
    }

    companion object {
        val TAG = ChatMessageAdapter::class.java.simpleName
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, itemType: Int): ChatMessageHolder {
        context = viewGroup.context
        if (itemType == ChatMessage.TYPE_SEND) {
            return ChatMessageHolder(LayoutInflater.from(viewGroup.context).inflate(R.layout.item_chat_send, viewGroup, false))
        } else if (itemType == ChatMessage.TYPE_RECEIVE) {
            return ChatMessageHolder(LayoutInflater.from(viewGroup.context).inflate(R.layout.item_chat_receive, viewGroup, false))
        }
        return ChatMessageHolder(LayoutInflater.from(viewGroup.context).inflate(R.layout.item_chat_send, viewGroup, false))
    }


    override fun onBindViewHolder(chatMessageHolder: ChatMessageAdapter.ChatMessageHolder, position: Int) {
        messageList?.let {
            chatMessageHolder.initView(it[position])
        }
    }

    override fun getItemViewType(position: Int): Int {
        messageList?.let {
            return it[position].messageType
        }
        return super.getItemViewType(position)
    }

    override fun getItemCount(): Int {
        return messageList?.size ?: 0
    }

    inner class ChatMessageHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var messageContent: TextView? = null
        var ivAvatar: ImageView? = null

        init {
            this.messageContent = itemView.findViewById(R.id.tv_item_message_content)
            this.ivAvatar = itemView.findViewById(R.id.iv_item_message_avatar)
        }

        fun initView(chatMessage: ChatMessage) {
            messageContent?.text = chatMessage.messageText
            if (chatMessage.messageType == ChatMessage.TYPE_SEND) {
                GlideApp.with(context!!).load(BmobUser.getCurrentUser(MyUser::class.java).avatar).circleCrop().into(ivAvatar!!)
            } else if (chatMessage.messageType == ChatMessage.TYPE_RECEIVE) {
                GlideApp.with(context!!).load(targetAvatar).circleCrop().into(ivAvatar!!)
            }
        }
    }

    fun addChatMessage(messageList: MutableList<ChatMessage>) {
        Log.d(TAG, "addChatMessage")
        messageList.forEach {
            this.messageList?.add(it)
        }
        notifyDataSetChanged()
    }
}
