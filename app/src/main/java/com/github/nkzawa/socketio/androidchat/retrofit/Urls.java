package com.github.nkzawa.socketio.androidchat.retrofit;

/**
 * Created by rubymobile on 21/04/16.
 */
public interface Urls {
    String LOGIN_URL = "/users/login";
    String CREATE_USER = "/users";
    String GET_USER_ID = "/users/{id}/get_user_info";

    String CREATE_GROUP = "/rooms";
    String ADD_USER_TO_GROUP = "/rooms/{id}/invite";
    String GET_ROOM_ID = "/rooms/{id}";

    String ADD_CONTACT = "/contacts";
}