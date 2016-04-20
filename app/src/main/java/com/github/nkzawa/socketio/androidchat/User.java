package com.github.nkzawa.socketio.androidchat;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rubymobile on 19/04/16.
 */
public class User {

    private String userId;
    private String name;

    public String getId() {
        return userId;
    }

    public void setId(String userId) {
        this.userId = userId;
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
        private final String mId;
        private String mName;

        public Builder(String id) {
            mId = id;
        }

        public Builder name(String name) {
            mName = name;
            return this;
        }

        public User build() {
            User user = new User();
            user.setId(mId);
            user.setName(mName);
            return user;
        }
    }

    public static User parseUser(JsonObject responseObject) {

        Builder userBuilder;
        userBuilder = new Builder(responseObject.get("userId").getAsString());

        if (responseObject.has("name") && !responseObject.get("name").isJsonNull()) {
            userBuilder.name(responseObject.get("name").getAsString());
        }


        return userBuilder.build();
    }

}
