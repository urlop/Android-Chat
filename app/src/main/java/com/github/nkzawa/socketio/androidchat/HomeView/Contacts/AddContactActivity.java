package com.github.nkzawa.socketio.androidchat.HomeView.Contacts;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.github.nkzawa.socketio.androidchat.Constants;
import com.github.nkzawa.socketio.androidchat.Models.Chat;
import com.github.nkzawa.socketio.androidchat.Models.Room;
import com.github.nkzawa.socketio.androidchat.Models.User;
import com.github.nkzawa.socketio.androidchat.PreferencesManager;
import com.github.nkzawa.socketio.androidchat.R;
import com.github.nkzawa.socketio.androidchat.retrofit.RestClient;
import com.google.gson.JsonObject;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class AddContactActivity extends ActionBarActivity {

    private EditText et_username;
    private Button btn_add_user;
    private RestClient restClient;
    private PreferencesManager mPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);
        restClient = new RestClient();
        mPreferences = PreferencesManager.getInstance(this);
        setupView();
        setupActions();
    }

    private void setupView(){
        et_username = (EditText)findViewById(R.id.et_username);
        btn_add_user = (Button)findViewById(R.id.btn_add_user);
    }

    private void setupActions(){
        btn_add_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addContact(et_username.getText().toString());
            }
        });
    }

    private void addContact(String contactUserName){
        JsonObject attributes = new JsonObject();
        attributes.addProperty("name", contactUserName);
        attributes.addProperty("owner_id", mPreferences.getUserId());
        restClient.getWebservices().addContact( attributes, new Callback<JsonObject>() {
            @Override
            public void success(JsonObject jsonObject, Response response) {

                User user = User.parseUser(jsonObject);
                user.save();
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_OK,returnIntent);
                finish();
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }
}
