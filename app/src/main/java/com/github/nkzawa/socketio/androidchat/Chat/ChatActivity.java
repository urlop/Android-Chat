package com.github.nkzawa.socketio.androidchat.Chat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.androidchat.ChatApplication;
import com.github.nkzawa.socketio.androidchat.HomeView.HomeActivity;
import com.github.nkzawa.socketio.androidchat.PreferencesManager;
import com.github.nkzawa.socketio.androidchat.R;
import com.github.nkzawa.socketio.client.Socket;


public class ChatActivity extends AppCompatActivity {

    private int receiverId;
    private int position;
    private String typeChat;
    private PreferencesManager mPreferences;
    public Socket mSocket;

    public String getTypeChat() {
        return typeChat;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ChatApplication app = (ChatApplication)getApplication();
        mSocket = app.getSocket();

        if(!mSocket.connected()){
            mSocket.connect();
        }

        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            receiverId = extras.getInt("receiverId");
            position = extras.getInt("position", 0);
            typeChat = extras.getString("typeChat");
        }


        mPreferences = PreferencesManager.getInstance(this);
        setContentView(R.layout.activity_main);

        Fragment fragment = new ChatFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.event_form_container, fragment);
        fragmentTransaction.commit();

    }

    @Override
    public void onResume() {
        super.onResume();
        mSocket.once(Socket.EVENT_CONNECT, onUserIsConnected);
        mSocket.on("activate user", onUserIsActivated);

        if(!mSocket.connected()){
            mSocket.connect();
        }

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mSocket.off(Socket.EVENT_CONNECT, onUserIsConnected);
        mSocket.off("activate user", onUserIsActivated);
    }


    public int getReceiverId() {
        return receiverId;
    }

    private Emitter.Listener onUserIsActivated = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "Conectado", Toast.LENGTH_SHORT).show();

                }
            });
        }
    };


    private Emitter.Listener onUserIsConnected = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d("user connected", " es : "+args.toString());
                    connectToServer();

                }
            });
        }
    };

    public void connectToServer(){
        Log.d("el socket es :", "ooo : "+mSocket.id());
        mSocket.emit("activate user", mPreferences.getUserId(), mSocket.id());
    }



    @Override
    public void onBackPressed() {

        Log.d("mejor","kjkaka");
        if(HomeActivity.getCheck_running_mode() == null){
            mSocket.disconnect();
            Intent intent = new Intent(this,HomeActivity.class);
            startActivity(intent);
        }else{
            Intent returnIntent = new Intent();
            returnIntent.putExtra("position",position);
            setResult(Activity.RESULT_OK,returnIntent);
        }


        finish();
    }
}
