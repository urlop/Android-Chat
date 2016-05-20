package com.github.nkzawa.socketio.androidchat.Models;

import com.github.nkzawa.socketio.androidchat.BuildConfig;
import com.google.gson.JsonObject;
import com.orm.SugarRecord;

/**
 * Created by rubymobile on 20/05/16.
 */
public class Attachment extends SugarRecord {

    private String localFileUrl;
    private String fileType;
    private String fileUrl;

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

        private String mFileUrl;
        private String mFileType;
        private int mMessageStatus;

        public Builder(String fileUrl) {
            mFileUrl = fileUrl;
        }


        public Builder fileUrl(String fileUrl) {
            mFileUrl = fileUrl;
            return this;
        }

        public Builder fileType(String fileType) {
            mFileType = fileType;
            return this;
        }

        public Builder messageStatus(int messageStatus){
            mMessageStatus = messageStatus;
            return this;
        }

        public Attachment build() {
            Attachment attachment = new Attachment();
            attachment.fileUrl = mFileUrl;
            attachment.fileType = mFileType;
            return attachment;

        }

    }

    public static Attachment parseAttachment(JsonObject jsonObject)  {
        Builder builder;
        builder=new Builder(jsonObject.get("media_file").getAsString());

        if(jsonObject.has("media_file_content_type") && !jsonObject.get("media_file_content_type").isJsonNull()){
            builder.fileType(jsonObject.get("media_file_content_type").getAsString());
        }

        return builder.build();
    }



}
