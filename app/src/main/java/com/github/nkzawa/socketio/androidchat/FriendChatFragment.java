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
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mSocket.disconnect();
    }


}
