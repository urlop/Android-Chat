package com.github.nkzawa.socketio.androidchat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.Date;

/**
 * Created by rubymobile on 20/04/16.
 */
public class PreferencesManager {

    private static final String PREFERENCES_NAME = "chatApp";
    private static final String USER_ID = "userId";
    private static final String USER_NAME = "userName";
    private static final String USER_INFO_LAST_UPDATE = "userInfoLastUpdate";

    private static PreferencesManager self;
    private final SharedPreferences mPreferences;
    private final Context context;

    private PreferencesManager(Context context) {
        this.context = context;
        mPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    public static PreferencesManager getInstance(Context context) {
        if (self == null) {
            self = new PreferencesManager(context);
        }

        return self;
    }

    public void saveUser(int userId, String userName){
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putInt(USER_ID, userId);
        editor.putString(USER_NAME, userName);
        editor.apply();
    }

    public int getUserId(){
        int userId = mPreferences.getInt(USER_ID, 0);
        return  userId;
    }

    public String getUserName(){
        String getUserName = mPreferences.getString(USER_NAME, "");
        return  getUserName;
    }

    public void saveLastUserInfoUpdate(Long lastUserInfoUpdate){
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putLong(USER_INFO_LAST_UPDATE, lastUserInfoUpdate);
        editor.apply();
    }

    public Date getLastUserInfoUpdate(){
        Long lastUserInfoUpdate = mPreferences.getLong(USER_INFO_LAST_UPDATE, 0l);
        return  new Date(lastUserInfoUpdate);
    }



}
