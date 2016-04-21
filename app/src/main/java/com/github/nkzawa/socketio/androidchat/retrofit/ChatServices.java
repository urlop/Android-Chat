package com.github.nkzawa.socketio.androidchat.retrofit;

import com.google.gson.JsonObject;

import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

/**
 * Created by rubymobile on 21/04/16.
 */
public interface ChatServices {

    @FormUrlEncoded
    @POST(Urls.LOGIN_URL)
    void loginUser(@Field("email") String name, @Field("password") String password, Callback<JsonObject> callback);

}
