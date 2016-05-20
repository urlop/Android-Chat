package com.github.nkzawa.socketio.androidchat.Models;

import com.github.nkzawa.socketio.androidchat.BuildConfig;
import com.github.nkzawa.socketio.androidchat.Constants;
import com.orm.SugarRecord;
import com.orm.dsl.Ignore;

public class Message extends SugarRecord {

    @Ignore
    public static final int MESSAGE_NOT_SENT = 0;
    @Ignore
    public static final int MESSAGE_SENT = 1;
    @Ignore
    public static final int MESSAGE_RECEIVED = 2;


    @Ignore
    public static final int TYPE_MESSAGE = 0;
    @Ignore
    public static final int TYPE_ACTION = 2;
    @Ignore
    private int mType = TYPE_MESSAGE;

    private String message;
    private String username;
    private Chat chat;
    private int messageStatus;
    private String receiverId;
    private Attachment attachment;


    public int getType() {
        return mType;
    };

    public String getMessage() {
        return message;
    };

    public String getUsername() {
        return username;
    };

    public Chat getChat() {
        return chat;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }

    public int getMessageStatus() {
        return messageStatus;
    }

    public void setMessageStatus(int messageStatus) {
        this.messageStatus = messageStatus;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public Attachment getAttachment() {
        return attachment;
    }

    public void setAttachment(Attachment attachment) {
        this.attachment = attachment;
    }

    public static class Builder {
        private final int mType;
        private String mUsername;
        private String mMessage;
        private String mReceiverId;
        private Chat mChat;
        private int mMessageStatus;

        public Builder(int type) {
            mType = type;
        }

        public Builder username(String username) {
            mUsername = username;
            return this;
        }

        public Builder message(String message) {
            mMessage = message;
            return this;
        }

        public Builder chat(Chat chat) {
            mChat = chat;
            return this;
        }

        public Builder receiverId(String receiverId){
            mReceiverId = receiverId;
            return this;
        }

        public Builder messageStatus(int messageStatus){
            mMessageStatus = messageStatus;
            return this;
        }

        public Message build() {
            Message message = new Message();
            message.mType = mType;
            message.username = mUsername;
            message.message = mMessage;
            message.chat = mChat;
            message.receiverId = mReceiverId;
            message.messageStatus = mMessageStatus;
            return message;

        }
    }
}
