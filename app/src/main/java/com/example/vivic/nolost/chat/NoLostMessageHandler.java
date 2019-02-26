package com.example.vivic.nolost.chat;

import android.content.Intent;

import com.example.vivic.nolost.NoLostApplication;
import com.example.vivic.nolost.bean.MyUser;
import com.example.vivic.nolost.bmob.ChatRepository;
import com.example.vivic.nolost.bmob.IBmobCallback;
import com.example.vivic.nolost.bmob.UserRepository;

import org.jetbrains.annotations.Nullable;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.event.MessageEvent;
import cn.bmob.newim.event.OfflineMessageEvent;
import cn.bmob.newim.listener.BmobIMMessageHandler;
import cn.bmob.newim.notification.BmobNotificationManager;

public class NoLostMessageHandler extends BmobIMMessageHandler {
    @Override
    public void onMessageReceive(final MessageEvent event) {
        //在线消息
        Intent intent = new Intent();
        intent.setAction("ACTION_FRIEND_ACTIVITY");
        BmobNotificationManager.getInstance(NoLostApplication.getContext()).showNotification(event, intent);
        //收到信息后，要讲对方的信息录入本地数据库，否则无法显示头像名称
        final BmobIMConversation conversation = event.getConversation();
        final BmobIMUserInfo info = event.getFromUserInfo();
        final BmobIMMessage msg = event.getMessage();
        String username = info.getName();
        String title = conversation.getConversationTitle();
        if (!username.equals(title)) {
            UserRepository.INSTANCE.queryUserByObject(info.getUserId(), new IBmobCallback<MyUser>() {
                @Override
                public void success(@Nullable MyUser user) {
                    ChatRepository.INSTANCE.updateUserInfo(user.getObjectId(), user.getUsername(), user.getAvatar());
                    conversation.setConversationIcon(user.getAvatar());
                    conversation.setConversationTitle(user.getUsername());
                    BmobIM.getInstance().updateConversation(conversation);
                }

                @Override
                public void error(@Nullable Throwable throwable) {

                }
            });
        }
    }

    @Override
    public void onOfflineReceive(final OfflineMessageEvent event) {
        //离线消息，每次connect的时候会查询离线消息，如果有，此方法会被调用
    }

}
