package com.github.nkzawa.socketio.androidchat;

import android.os.Bundle;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.Socket;
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

        mSocket.disconnect();
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


}
