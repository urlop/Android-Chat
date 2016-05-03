package com.github.nkzawa.socketio.androidchat.Models;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import com.orm.SugarRecord;
import com.orm.dsl.Ignore;

import java.io.Serializable;

/**
 * Created by rubymobile on 19/04/16.
 */
public class User extends SugarRecord{


    // Saved data in database
    private int userId;
    private String name;
    private String socket_id;


    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getSocket_id() {
        return socket_id;
    }

    public void setSocket_id(String socket_id) {
        this.socket_id = socket_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User() {

    }

    public static class Builder {
        private int mId;
        private String mName;
        private String mSocket_id;

        public Builder(int id) {
            mId = id;
        }

        public Builder name(String name) {
            mName = name;
            return this;
        }

        public Builder sockedId(String sockedId) {
            mSocket_id = sockedId;
            return this;
        }

        public User build() {
            User user = new User();
            user.setUserId(mId);
            user.setName(mName);
            user.setSocket_id(mSocket_id);
            return user;
        }
    }

    public static User parseUser(JsonObject responseObject) {

        Builder userBuilder;
        userBuilder = new Builder(responseObject.get("id").getAsInt());

        if (responseObject.has("name") && !responseObject.get("name").isJsonNull()) {
            userBuilder.name(responseObject.get("name").getAsString());
        }

        if (responseObject.has("socket_id") && !responseObject.get("socket_id").isJsonNull()) {
            userBuilder.sockedId(responseObject.get("socket_id").getAsString());
        }


        return userBuilder.build();
    }

}
