package com.github.nkzawa.socketio.androidchat.Chat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;

import com.github.nkzawa.socketio.androidchat.PreferencesManager;
import com.github.nkzawa.socketio.androidchat.R;


public class ChatActivity extends ActionBarActivity {

    private int receiverId;
    private int position;
    private String typeChat;
    private PreferencesManager mPreferences;

    public String getTypeChat() {
        return typeChat;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            receiverId = extras.getInt("receiverId");
            position = extras.getInt("position", 0);
            typeChat = extras.getString("typeChat");
        }


        mPreferences = PreferencesManager.getInstance(this);
        setContentView(R.layout.activity_main);

        Fragment fragment = new ChatFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.event_form_container, fragment);
        fragmentTransaction.commit();

    }


    public int getReceiverId() {
        return receiverId;
    }

    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("position",position);
        setResult(Activity.RESULT_OK,returnIntent);
        finish();
    }
}
