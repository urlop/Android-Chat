package com.github.nkzawa.socketio.androidchat;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;


public class MainActivity extends ActionBarActivity {

    String receiverName;
    int numUsers;
    private PreferencesManager mPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            receiverName = ""+extras.getInt("receiverName");
            numUsers = extras.getInt("numUsers", 0);
        }


        mPreferences = PreferencesManager.getInstance(this);
        setContentView(R.layout.activity_main);
    }

    public int getNumUsers() {
        return numUsers;
    }

    public void setNumUsers(int numUsers) {
        this.numUsers = numUsers;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }
}
