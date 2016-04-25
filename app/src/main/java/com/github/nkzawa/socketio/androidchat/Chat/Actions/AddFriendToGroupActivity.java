package com.github.nkzawa.socketio.androidchat.Chat.Actions;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.github.nkzawa.socketio.androidchat.HomeView.HomeAdapter;
import com.github.nkzawa.socketio.androidchat.Models.User;
import com.github.nkzawa.socketio.androidchat.R;

import java.util.ArrayList;
import java.util.List;

public class AddFriendToGroupActivity extends Activity {

    RecyclerView rv_friends;
    List<User> users = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend_to_group);
        setupView();
    }

    public void setupView(){
        rv_friends = (RecyclerView)findViewById(R.id.rv_friends);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rv_friends.setLayoutManager(layoutManager);
        FriendsAdapter adapter = new FriendsAdapter(users);
        rv_friends.setAdapter(adapter);

    }
}
