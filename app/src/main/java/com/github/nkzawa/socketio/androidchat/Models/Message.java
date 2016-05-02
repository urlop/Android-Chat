package com.github.nkzawa.socketio.androidchat.Models;

import com.orm.SugarRecord;
import com.orm.dsl.Ignore;

public class Message extends SugarRecord {

    @Ignore
    public static final int TYPE_MESSAGE = 0;
    @Ignore
    public static final int TYPE_LOG = 1;
    @Ignore
    public static final int TYPE_ACTION = 2;

    private int mType;
    private String mMessage;
    private String mUsername;
    private Chat chat;

    private Message() {}

    public int getType() {
        return mType;
    };

    public String getMessage() {
        return mMessage;
    };

    public String getUsername() {
        return mUsername;
    };

    public Chat getChat() {
        return chat;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }

    public static class Builder {
        private final int mType;
        private String mUsername;
        private String mMessage;
        private Chat mChat;

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

        public Message build() {
            Message message = new Message();
            message.mType = mType;
            message.mUsername = mUsername;
            message.mMessage = mMessage;
            return message;
        }
    }
}
