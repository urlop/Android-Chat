package com.github.nkzawa.socketio.androidchat.HomeView;

import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;

import com.github.nkzawa.socketio.androidchat.Chat.ChatFragment;
import com.github.nkzawa.socketio.androidchat.Constants;
import com.github.nkzawa.socketio.androidchat.Models.Chat;
import com.github.nkzawa.socketio.androidchat.Models.Room;
import com.github.nkzawa.socketio.androidchat.PreferencesManager;
import com.github.nkzawa.socketio.androidchat.R;
import com.github.nkzawa.socketio.androidchat.Models.User;
import com.github.nkzawa.socketio.androidchat.retrofit.RestClient;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class HomeActivity extends ActionBarActivity {


    private RestClient restClient;
    private PreferencesManager mPreferences;
    private FragmentTabHost mTabHost;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        restClient = new RestClient();
        mPreferences = PreferencesManager.getInstance(this);

        setupView();
        getUserInfo();

    }


    public void setupView(){

        mTabHost = (FragmentTabHost)findViewById(android.R.id.tabhost);

        mTabHost.setup(this, getSupportFragmentManager(), android.R.id.tabcontent);

        mTabHost.addTab(
                mTabHost.newTabSpec("Chat").setIndicator("Chat", null),
                ChatsFragment.class, null);
        mTabHost.addTab(
                mTabHost.newTabSpec("Contacts").setIndicator("Contacts", null),
                ContactsFragment.class, null);


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
                    Chat.createChat(room.getRoomId(), Constants.ROOM_CHAT);
                }

                ChatsFragment currentFragment = (ChatsFragment)getSupportFragmentManager().findFragmentByTag("Chat");
                currentFragment.setContacts();
//                //                Date lastDate = mPreferences.getLastUserInfoUpdate();
//
//                Date currentDate = new Date();
//                mPreferences.saveLastUserInfoUpdate(currentDate.getTime());

            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }
}
