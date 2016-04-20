package com.github.nkzawa.socketio.androidchat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Created by rubymobile on 20/04/16.
 */
public class PreferencesManager {

    private static final String PREFERENCES_NAME = "chatApp";
    private static final String USER_ID = "userId";
    private static final String USER_NAME = "userName";

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

    public void saveUser(String userId, String userName){
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(USER_ID, userId);
        editor.putString(USER_NAME, userName);
        editor.apply();
    }

    public String getUserId(){
        String userId = mPreferences.getString(USER_ID, "");
        return  userId;
    }



}
