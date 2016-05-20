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

    private String localFileUrl;
    private String fileType;
    private String fileUrl;


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

    public String getLocalFileUrl() {
        return localFileUrl;
    }

    public void setLocalFileUrl(String localFileUrl) {
        this.localFileUrl = localFileUrl;
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


    public static class Builder {
        private final int mType;
        private String mUsername;
        private String mMessage;
        private String mReceiverId;
        private Chat mChat;
        private String mFileUrl;
        private String mFileType;
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
