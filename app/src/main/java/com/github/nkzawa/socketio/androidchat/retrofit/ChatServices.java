package com.github.nkzawa.socketio.androidchat.retrofit;

import com.google.gson.JsonObject;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;

/**
 * Created by rubymobile on 21/04/16.
 */
public interface ChatServices {


    @POST(Urls.LOGIN_URL)
    void loginUser(@Body Object body , Callback<JsonObject> callback);

    @POST(Urls.CREATE_USER)
    void createUser(@Body Object body, Callback<JsonObject> callback);

    @POST(Urls.CREATE_GROUP)
    void createGroup(@Body Object body, Callback<JsonObject> callback);

    @FormUrlEncoded
    @POST(Urls.ADD_USER_TO_GROUP)
    void addUserToRoom(@Path("id") int id, @Field("user_id") int userId, Callback<JsonObject> callback);

    @GET(Urls.GET_USER_ID)
    void getUserInfo(@Path("id") int id, Callback<JsonObject> callback);

    @POST(Urls.ADD_CONTACT)
    void addContact(@Body Object body, Callback<JsonObject> callback);

}
