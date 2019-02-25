package com.example.vivic.nolost.chat;

public class ChatMessage {
    public static int TYPE_SEND = 0;
    public static int TYPE_RECEIVE = 1;

    public int messageType;
    public String messageText;

    public ChatMessage(int messageType, String messageText) {
        this.messageType = messageType;
        this.messageText = messageText;
    }

    @Override
    public String toString() {
        return "ChatMessage"
                + "--messageType:" + messageType
                + "--messageText:" + messageText;
    }
}
