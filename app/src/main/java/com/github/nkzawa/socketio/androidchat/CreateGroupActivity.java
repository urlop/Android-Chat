package com.github.nkzawa.socketio.androidchat;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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

                Log.d("mylog","userId: "+userId);
                Log.d("mylog","groupName: "+groupname);

                mSocket.emit("join room", userId,groupname);
                Log.d("mylog","success: "+userId);


            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


}
