package com.github.nkzawa.socketio.androidchat.Models;

import com.github.nkzawa.socketio.androidchat.BuildConfig;
import com.github.nkzawa.socketio.androidchat.Constants;
import com.orm.SugarRecord;
import com.orm.dsl.Ignore;

public class Message extends SugarRecord {

    @Ignore
    public static final int TYPE_MESSAGE = 0;
    @Ignore
    public static final int TYPE_LOG = 1;
    @Ignore
    public static final int TYPE_ACTION = 2;
    @Ignore
    private int mType = TYPE_MESSAGE;

    private String message;
    private String username;
    private Chat chat;
    private String fileUrl;

    private String localFileUrl;
    private String fileType;
    private String receiverId;

    public String getLocalFileUrl() {
        return localFileUrl;
    }

    public void setLocalFileUrl(String localFileUrl) {
        this.localFileUrl = localFileUrl;
    }

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

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getFileUrl() {
        return BuildConfig.BASE_URL+fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }

    public static class Builder {
        private final int mType;
        private String mUsername;
        private String mMessage;
        private String mFileUrl;
        private String mFileType;
        private String mReceiverId;
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

        public Builder fileUrl(String fileUrl) {
            mFileUrl = fileUrl;
            return this;
        }

        public Builder fileType(String fileType) {
            mFileType = fileType;
            return this;
        }

        public Builder receiverId(String receiverId){
            mReceiverId = receiverId;
            return this;
        }

        public Message build() {
            Message message = new Message();
            message.mType = mType;
            message.username = mUsername;
            message.message = mMessage;
            message.chat = mChat;
            message.fileUrl = mFileUrl;
            message.fileType = mFileType;
            message.receiverId = mReceiverId;
            return message;

        }
    }
}
