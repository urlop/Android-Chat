package com.github.nkzawa.socketio.androidchat.Authentication;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.github.nkzawa.socketio.androidchat.Chat.ChatApplication;
import com.github.nkzawa.socketio.androidchat.R;
import com.github.nkzawa.socketio.androidchat.retrofit.RestClient;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import io.socket.client.Socket;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class CreateUserActivity extends Activity {

    private Socket mSocket;
    private EditText et_username;
    private Button btn_create;
    private RestClient restClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);
        ChatApplication app = (ChatApplication) getApplication();
        mSocket = app.getSocket();
        restClient = new RestClient();
        setupView ();
        setupActions();
    }


    private void setupView (){
        et_username = (EditText)findViewById(R.id.et_username);
        btn_create = (Button) findViewById(R.id.btn_create);
    }

    private void setupActions(){
        btn_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("aaaa","aaaa");
                String username = et_username.getText().toString();
                createUser(username);
            }
        });
    }

    private void createUser(String username){
        JsonObject user = new JsonObject();
        JsonObject attributes = new JsonObject();
        attributes.addProperty("name", username);
        user.add("user",attributes);

        restClient.getWebservices().createUser( user, new Callback<JsonObject>() {
            @Override
            public void success(JsonObject jsonObject, Response response) {

                Log.d("aaaa","aaaa2");
                JsonObject me = jsonObject.get("me").getAsJsonObject();
                String users = jsonObject.get("users").getAsJsonArray().toString();
                String rooms = jsonObject.get("rooms").getAsJsonArray().toString();
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }
}
