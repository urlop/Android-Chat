package com.github.nkzawa.socketio.androidchat.retrofit;

import com.google.gson.JsonObject;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

/**
 * Created by rubymobile on 21/04/16.
 */
public interface ChatServices {


    @POST(Urls.LOGIN_URL)
    void loginUser(@Body Object body , Callback<JsonObject> callback);

    @POST(Urls.CREATE_USER)
    void createUser(@Body Object body, Callback<JsonObject> callback);

}
