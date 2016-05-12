package com.github.nkzawa.socketio.androidchat.retrofit;

import com.google.gson.JsonObject;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.Path;
import retrofit.mime.TypedFile;

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

    @Multipart
    @POST(Urls.MESSAGE)
    void sendMessage(@Part("message[sender_id]") String sender_id ,@Part("message[receiver_user_id]") String receiver_user_id,@Part("message[receiver_room_id]") String receiver_room_id, @Part("message[media_file]") TypedFile image,@Part("message[content]") String content ,Callback<JsonObject> callback);

}
