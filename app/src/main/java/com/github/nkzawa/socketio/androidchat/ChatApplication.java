package com.github.nkzawa.socketio.androidchat;

import android.app.Application;

import com.github.nkzawa.socketio.androidchat.Constants;
import com.orm.SugarApp;


import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

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
