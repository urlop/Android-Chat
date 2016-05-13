package com.github.nkzawa.socketio.androidchat.Authentication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.github.nkzawa.socketio.androidchat.ChatApplication;
import com.github.nkzawa.socketio.androidchat.HomeView.HomeActivity;
import com.github.nkzawa.socketio.androidchat.Models.Room;
import com.github.nkzawa.socketio.androidchat.Models.User;
import com.github.nkzawa.socketio.androidchat.PreferencesManager;
import com.github.nkzawa.socketio.androidchat.R;
import com.github.nkzawa.socketio.androidchat.retrofit.RestClient;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import io.socket.client.Socket;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class CreateUserActivity extends Activity {

    private EditText et_username;
    private Button btn_create;
    private RestClient restClient;
    private PreferencesManager mPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);
        restClient = new RestClient();
        mPreferences = PreferencesManager.getInstance(this);
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

                JsonObject me = jsonObject.get("me").getAsJsonObject();

                String name = me.get("name").getAsString();
                int userId = me.get("id").getAsInt();
                mPreferences.saveUser(userId,name);

                JsonArray jsonArray = null;
                jsonArray = jsonObject.get("users").getAsJsonArray();

                for (JsonElement jsonElement : jsonArray) {
                    JsonObject jsonObjectUser = jsonElement.getAsJsonObject();
                    User user = User.parseUser(jsonObjectUser);
                    user.save();
                }

                jsonArray = jsonObject.get("rooms").getAsJsonArray();

                for (JsonElement jsonElement : jsonArray) {
                    JsonObject jsonObjectRoom = jsonElement.getAsJsonObject();
                    Room room = Room.parseRoom(jsonObjectRoom);
                    room.save();
                }

                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }
}
