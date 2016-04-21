package com.github.nkzawa.socketio.androidchat.HomeView.Groups;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.github.nkzawa.socketio.androidchat.Chat.ChatApplication;
import com.github.nkzawa.socketio.androidchat.Constants;
import com.github.nkzawa.socketio.androidchat.Chat.MainActivity;
import com.github.nkzawa.socketio.androidchat.PreferencesManager;
import com.github.nkzawa.socketio.androidchat.R;

import io.socket.client.Socket;

public class CreateGroupActivity extends Activity {

    Button btn_create;
    EditText et_group_name;
    private Socket mSocket;
    private PreferencesManager mPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        mPreferences = PreferencesManager.getInstance(this);
        btn_create = (Button) findViewById(R.id.btn_create);
        et_group_name = (EditText) findViewById(R.id.et_group_name);

        ChatApplication app = (ChatApplication) getApplication();
        mSocket = app.getSocket();
        setupVoid();
    }


    private void setupVoid(){
        btn_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String groupname = et_group_name.getText().toString();
                String userId = mPreferences.getUserId();

                mSocket.emit("join room", userId,groupname);

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("receiverName", groupname);
                intent.putExtra("numUsers", 1);
                intent.putExtra("typeChat", Constants.GROUP_CHAT);
                startActivity(intent);
                finish();


            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


}
