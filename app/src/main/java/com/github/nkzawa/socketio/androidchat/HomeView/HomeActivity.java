package com.github.nkzawa.socketio.androidchat.HomeView;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.github.nkzawa.socketio.androidchat.ChatApplication;
import com.github.nkzawa.socketio.androidchat.HomeView.Groups.CreateGroupActivity;
import com.github.nkzawa.socketio.androidchat.Models.Room;
import com.github.nkzawa.socketio.androidchat.PreferencesManager;
import com.github.nkzawa.socketio.androidchat.R;
import com.github.nkzawa.socketio.androidchat.Models.User;
import com.github.nkzawa.socketio.androidchat.retrofit.RestClient;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class HomeActivity extends ActionBarActivity {

    RecyclerView rv_friends;
    List<Object> contactsList = new ArrayList<>();
    int numUsers;
    Button btn_create_group;
    public Socket mSocket;
    private PreferencesManager mPreferences;
    Bundle extras;
    private RestClient restClient;



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
        restClient = new RestClient();
        extras = getIntent().getExtras();


        setupView();
        getUserInfo();

    }


    public void setupView(){
        rv_friends = (RecyclerView)findViewById(R.id.rv_friends);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rv_friends.setLayoutManager(layoutManager);

        btn_create_group = (Button)findViewById(R.id.btn_create_group);

        btn_create_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CreateGroupActivity.class);
                startActivity(intent);
            }
        });
        setContacts();
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

    private void setContacts(){
        List<User> friends = User.listAll(User.class);
        List<Room> rooms = Room.listAll(Room.class);
        contactsList.addAll(friends);
        contactsList.addAll(rooms);

        HomeAdapter adapter = new HomeAdapter(this, contactsList);
        rv_friends.setAdapter(adapter);
    }

    public void getUserInfo(){
        restClient.getWebservices().getUserInfo(mPreferences.getUserId(), new Callback<JsonObject>() {
            @Override
            public void success(JsonObject jsonObject, Response response) {

                User.deleteAll(User.class);
                Room.deleteAll(Room.class);

                Log.d("response", "resoibes "+jsonObject.toString());
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
                    Room room = Room.parseGroup(jsonObjectRoom, mPreferences);
                    room.save();
                }


            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }
}
