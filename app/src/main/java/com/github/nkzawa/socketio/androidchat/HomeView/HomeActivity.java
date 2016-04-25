package com.github.nkzawa.socketio.androidchat.HomeView;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.github.nkzawa.socketio.androidchat.Chat.ChatApplication;
import com.github.nkzawa.socketio.androidchat.HomeView.Groups.CreateGroupActivity;
import com.github.nkzawa.socketio.androidchat.Models.Group;
import com.github.nkzawa.socketio.androidchat.PreferencesManager;
import com.github.nkzawa.socketio.androidchat.R;
import com.github.nkzawa.socketio.androidchat.Models.User;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class HomeActivity extends ActionBarActivity {

    RecyclerView rv_friends;
    List<Object> contactsList = new ArrayList<>();
    int numUsers;
    Button btn_create_group;
    public Socket mSocket;
    private PreferencesManager mPreferences;
    Bundle extras;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        mPreferences = PreferencesManager.getInstance(this);
        ChatApplication app = (ChatApplication)getApplication();
        mSocket = app.getSocket();
        mSocket.connect();
        mSocket.on(Socket.EVENT_CONNECT, onUserIsConnected);
        mSocket.on("activate user", onUserIsActivated);

        extras = getIntent().getExtras();


        setupView();

    }


    public void setupView(){
        rv_friends = (RecyclerView)findViewById(R.id.rv_friends);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rv_friends.setLayoutManager(layoutManager);
        HomeAdapter adapter = new HomeAdapter(this, contactsList);
        rv_friends.setAdapter(adapter);

        btn_create_group = (Button)findViewById(R.id.btn_create_group);

        btn_create_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CreateGroupActivity.class);
                startActivity(intent);
            }
        });

    }

    public void connectToServer(){
        Log.d("el socket es :", "ooo : "+mSocket.id());
        mSocket.emit("activate user", mPreferences.getUserId(), mSocket.id());
    }

    private Emitter.Listener onUserIsActivated = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d("user activated", " es : "+args[0]);
                    Log.d("el socket es :", "ooo 2 : "+mSocket.id());
                    setContacts();
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

    private void setContacts(){
        if(extras != null) {
            String friends = extras.getString("usersList");
            JsonParser jsonParser = new JsonParser();
            JsonArray jsonArray = null;
            jsonArray = (JsonArray)jsonParser.parse(friends);

            for (JsonElement jsonElement : jsonArray) {
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                User user = User.parseUser(jsonObject);
                contactsList.add(user);
            }

            String groups = extras.getString("groupsList");
            jsonArray = (JsonArray)jsonParser.parse(groups);

            for (JsonElement jsonElement : jsonArray) {
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                Group group = Group.parseGroup(jsonObject);
                contactsList.add(group);
            }


            numUsers =  extras.getInt("numUsers");
        }
    }
}
