package com.github.nkzawa.socketio.androidchat;

import android.app.Application;

import com.github.nkzawa.socketio.androidchat.Constants;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.orm.SugarApp;


import java.net.URISyntaxException;



public class ChatApplication extends SugarApp {

    private Socket mSocket;
    {
        try {
            mSocket = IO.socket(Constants.CHAT_SERVER_URL);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public Socket getSocket() {
        return mSocket;
    }
}
