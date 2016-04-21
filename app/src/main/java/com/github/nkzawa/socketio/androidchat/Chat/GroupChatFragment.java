package com.github.nkzawa.socketio.androidchat.Chat;

import android.os.Bundle;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.emitter.Emitter;

/**
 * Created by rubymobile on 20/04/16.
 */
public class GroupChatFragment extends MainFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSocket.on("message sent room", onMessageRoom);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSocket.off("message sent room", onMessageRoom);

        mSocket.disconnect();
    }



    private Emitter.Listener onMessageRoom = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {

            Log.d("llega rooom" ,""+args[0].toString());
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String username;
                    String message;
                    try {
                        username = data.getString("userId");
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
        Log.d("aaaa","antes1 "+receiverName);
        Log.d("aaaa","adespues2 "+receiverName);
        mSocket.emit("send message room", messageToSend,receiverName);
    }


}

