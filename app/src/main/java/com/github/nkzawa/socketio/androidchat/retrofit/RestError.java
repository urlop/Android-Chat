package com.github.nkzawa.socketio.androidchat.retrofit;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

/**
 * Created by rubymobile on 21/04/16.
 */
public class RestError {

    @SerializedName("error")
    private JsonObject error;


    public int getCode() {
        int code = -1;
        if(error.has("code")){
            code = error.get("code").getAsInt();
        }
        return code;
    }

    public String getMessage() {
        String message = "";
        if(error.has("message")){
            message = error.get("message").getAsString();
        }
        return message;
    }

    public String getReasons() {
        String reasons = "";
        if(error.has("reasons")){
            JsonArray jsonArray = error.get("reasons").getAsJsonArray();
            for (JsonElement jsonElement : jsonArray){
                reasons = reasons + jsonElement.getAsString() + ",";
            }
        }
        reasons = reasons.substring(0,reasons.length()-1);
        return reasons;
    }
}
