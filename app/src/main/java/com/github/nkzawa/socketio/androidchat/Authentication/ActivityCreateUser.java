package com.github.nkzawa.socketio.androidchat.Authentication;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;

import com.github.nkzawa.socketio.androidchat.Chat.ChatApplication;

import io.socket.client.Socket;

public class ActivityCreateUser extends Activity {

    private Socket mSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ChatApplication app = (ChatApplication) getApplication();
        mSocket = app.getSocket();
    }
}
