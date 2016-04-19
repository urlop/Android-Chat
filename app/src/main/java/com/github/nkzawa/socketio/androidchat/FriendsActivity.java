package com.github.nkzawa.socketio.androidchat;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class FriendsActivity extends ActionBarActivity {

    RecyclerView rv_friends;
    List<User> friendsUser = new ArrayList<>();
    String mUsername;
    int numUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            Gson gson = new Gson();
            String friends = extras.getString("friendsList");
            JsonParser jsonParser = new JsonParser();
            JsonArray jsonArray = (JsonArray)jsonParser.parse(friends);

            for (JsonElement jsonElement : jsonArray) {
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                Log.d("harryuuuuu", "mirame "+ jsonObject.toString());
                User user = User.parseUser(jsonObject);
                friendsUser.add(user);
            }

            mUsername = "";

            numUsers =  extras.getInt("numUsers");
            mUsername = extras.getString("username");

        }

        setupView();

    }


    public void setupView(){
        rv_friends = (RecyclerView)findViewById(R.id.rv_friends);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rv_friends.setLayoutManager(layoutManager);
        FriendsAdapter adapter = new FriendsAdapter(this, friendsUser);
        rv_friends.setAdapter(adapter);


    }
}
