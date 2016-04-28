package com.github.nkzawa.socketio.androidchat.Chat;

import android.os.Bundle;
import android.util.Log;

import com.github.nkzawa.socketio.androidchat.Models.User;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.emitter.Emitter;

/**
 * Created by rubymobile on 20/04/16.
 */
public class FriendChatFragment extends MainFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("aaaa","k9aasdasd ");
        mSocket.on("message sent", onMessageSent);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSocket.off("message sent", onMessageSent);
    }


    private Emitter.Listener onMessageSent = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {

            Log.d("aaaa","antes1 "+args[0]);
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String username;
                    String message;
                    try {
                        Log.d("aaaa","antes1 "+args[0]);
                        JsonParser jsonParser = new JsonParser();
                        JsonObject gsonObject = (JsonObject)jsonParser.parse(data.toString());
                        JsonObject userJsonObj = gsonObject.getAsJsonObject("user");
                        User user = User.parseUser(userJsonObj);
                        username = user.getName();
                        message = data.getString("message");
                    } catch (JSONException e) {
                        return;
                    }

                    removeTyping(username);
                    addMessage(username, message);
                }
            });
        }
    };


    @Override
    protected void attemptSend(){
        super.attemptSend();
        Log.d("see enviaa", "tu :"+messageToSend + "-" + receiverId+ "-" + "user");
        mSocket.emit("send message", messageToSend, receiverId, "user");
    }


}
