package com.github.nkzawa.socketio.androidchat.HomeView.Groups;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.github.nkzawa.socketio.androidchat.ChatApplication;
import com.github.nkzawa.socketio.androidchat.PreferencesManager;
import com.github.nkzawa.socketio.androidchat.R;
import com.github.nkzawa.socketio.androidchat.retrofit.RestClient;
import com.google.gson.JsonObject;

import io.socket.client.Socket;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class CreateGroupActivity extends Activity {

    Button btn_create;
    EditText et_group_name;
    private Socket mSocket;
    private PreferencesManager mPreferences;
    private RestClient restClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        mPreferences = PreferencesManager.getInstance(this);
        btn_create = (Button) findViewById(R.id.btn_create);
        et_group_name = (EditText) findViewById(R.id.et_group_name);
        restClient = new RestClient();

        ChatApplication app = (ChatApplication) getApplication();
        mSocket = app.getSocket();
        setupVoid();
    }


    private void setupVoid(){
        btn_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String groupname = et_group_name.getText().toString();
                createGroup(groupname);
//                mSocket.emit("join room", userId,groupname);
//
//                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//                intent.putExtra("receiverId", groupname);
//                intent.putExtra("numUsers", 1);
//                intent.putExtra("typeChat", Constants.GROUP_CHAT);
//                startActivity(intent);
//                finish();


            }
        });
    }

    private void createGroup(String groupname){
        JsonObject group = new JsonObject();
        JsonObject attributes = new JsonObject();
        attributes.addProperty("name", groupname);
        attributes.addProperty("owner_id", mPreferences.getUserId());
        group.add("room",attributes);

        restClient.getWebservices().createGroup( group, new Callback<JsonObject>() {
            @Override
            public void success(JsonObject jsonObject, Response response) {

                Log.d("aaaa","aaaa2"+ response);

            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


}
