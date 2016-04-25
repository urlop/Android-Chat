package com.github.nkzawa.socketio.androidchat.Chat;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;

import com.github.nkzawa.socketio.androidchat.Constants;
import com.github.nkzawa.socketio.androidchat.PreferencesManager;
import com.github.nkzawa.socketio.androidchat.R;


public class MainActivity extends ActionBarActivity {

    private int receiverId;
    private int numUsers;
    private String typeChat;
    private PreferencesManager mPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            receiverId = extras.getInt("receiverId");
            numUsers = extras.getInt("numUsers", 0);
            typeChat = extras.getString("typeChat");
        }


        mPreferences = PreferencesManager.getInstance(this);
        setContentView(R.layout.activity_main);

        Fragment fragment = null;
        if(typeChat.equals(Constants.USER_CHAT)){
            fragment = new FriendChatFragment();
        }else {
            fragment = new GroupChatFragment();
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.event_form_container, fragment);
        fragmentTransaction.commit();

    }

    public int getNumUsers() {
        return numUsers;
    }

    public void setNumUsers(int numUsers) {
        this.numUsers = numUsers;
    }

    public int getReceiverId() {
        return receiverId;
    }
}
