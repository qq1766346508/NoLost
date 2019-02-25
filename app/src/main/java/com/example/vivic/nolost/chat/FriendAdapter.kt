package com.example.vivic.nolost.chat

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import cn.bmob.newim.bean.BmobIMConversation
import com.example.vivic.nolost.GlideApp
import com.example.vivic.nolost.R

class FriendAdapter : RecyclerView.Adapter<FriendAdapter.ViewHolder>() {

    var friendList: MutableList<BmobIMConversation>? = null
    var context: Context? = null

    init {
        friendList = mutableListOf()
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        context = viewGroup.context
        return ViewHolder(LayoutInflater.from(viewGroup.context).inflate(R.layout.item_friend, viewGroup, false))
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        friendList?.let {
            viewHolder.initView(it[position])
        }
    }

    override fun getItemCount(): Int {
        return friendList?.size ?: 0
    }

    fun addFriend(list: MutableList<BmobIMConversation>?) {
        list?.let {
            friendList?.addAll(it)
            notifyDataSetChanged()
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var ivAvatar: ImageView? = null
        private var tvName: TextView? = null
        private var tvCount: TextView? = null

        init {
            ivAvatar = itemView.findViewById(R.id.tv_item_friend_avatar)
            tvName = itemView.findViewById(R.id.tv_item_friend_name)
            tvCount = itemView.findViewById(R.id.tv_item_friend_count)
        }

        fun initView(bmobIMConversation: BmobIMConversation) {
            GlideApp.with(context!!).load(bmobIMConversation.conversationIcon).circleCrop().into(ivAvatar!!)
            tvName?.text = bmobIMConversation.conversationTitle
        }
    }
}
