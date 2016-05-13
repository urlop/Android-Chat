package com.github.nkzawa.socketio.androidchat.HomeView;

import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.github.nkzawa.socketio.androidchat.Chat.ChatFragment;
import com.github.nkzawa.socketio.androidchat.ChatApplication;
import com.github.nkzawa.socketio.androidchat.ChatUtilsMethods;
import com.github.nkzawa.socketio.androidchat.Constants;
import com.github.nkzawa.socketio.androidchat.HomeView.Chats.ChatsFragment;
import com.github.nkzawa.socketio.androidchat.HomeView.Contacts.ContactsFragment;
import com.github.nkzawa.socketio.androidchat.Models.Chat;
import com.github.nkzawa.socketio.androidchat.Models.Message;
import com.github.nkzawa.socketio.androidchat.Models.Room;
import com.github.nkzawa.socketio.androidchat.PreferencesManager;
import com.github.nkzawa.socketio.androidchat.R;
import com.github.nkzawa.socketio.androidchat.Models.User;
import com.github.nkzawa.socketio.androidchat.retrofit.RestClient;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONObject;

import java.util.List;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class HomeActivity extends ActionBarActivity {


    private RestClient restClient;
    private PreferencesManager mPreferences;
    private FragmentTabHost mTabHost;
    private Socket mSocket;
    private ChatApplication app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        restClient = new RestClient();
        mPreferences = PreferencesManager.getInstance(this);
        app = (ChatApplication)getApplication();
        mSocket = app.getSocket();
        mSocket.connect();

        setupView();
        getUserInfo();

    }

    @Override
    public void onResume() {
        super.onResume();
        mSocket.on(Socket.EVENT_CONNECT, onUserIsConnected);
        mSocket.on("activate user", onUserIsActivated);
        mSocket.on("message sent", onMessageSent);
    }

    @Override
    public void onPause() {
        super.onPause();
        mSocket.off(Socket.EVENT_CONNECT_ERROR, onUserIsConnected);
        mSocket.off("activate user", onUserIsActivated);
        mSocket.off("message sent", onMessageSent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("mejor","kjkaka");
        mSocket.disconnect();
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
                    Room room = Room.parseRoom(jsonObjectRoom);
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


    private Emitter.Listener onMessageSent = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {

            Log.d("aaaa","antes1 "+args[0]);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String username;
                    String message;
                    JsonParser jsonParser = new JsonParser();
                    JsonObject gsonObject = (JsonObject)jsonParser.parse(data.toString());
                    JsonObject userJsonObj = gsonObject.getAsJsonObject("sender");
                    User user = User.parseUser(userJsonObj);
                    username = user.getName();
                    message = gsonObject.get("content").getAsString();
                    String lastMessage = "";

                    Chat chat = null;
                    if(gsonObject.has("receiver_room_id")){
                        chat = Chat.createChat(gsonObject.get("receiver_room_id").getAsInt(), Constants.ROOM_CHAT);
                        lastMessage = ""+username+": "+message;
                    }else{
                        if(gsonObject.has("sender")){
                            List<User> users = User.find(User.class, "user_id = ?", ""+user.getUserId());
                            if (users.isEmpty()){
                                user.save();
                                if(ContactsFragment.class.isInstance(getSupportFragmentManager().findFragmentByTag("Contacts"))){
                                    ContactsFragment contactsFragment = (ContactsFragment)getSupportFragmentManager().findFragmentByTag("Contacts");
                                    contactsFragment.setContacts();
                                }

                            }
                            chat = Chat.createChat(user.getUserId(),Constants.USER_CHAT);
                        }
                        lastMessage = message;
                    }

                    chat.setLastMessage(lastMessage);
                    chat.save();

                    Message receiveMessage = new Message.Builder(Message.TYPE_MESSAGE).username(username).message(message).build();
                    receiveMessage.setChat(chat);
                    receiveMessage.save();

                    if(ChatsFragment.class.isInstance(getSupportFragmentManager().findFragmentByTag("Chat"))){
                        ChatsFragment chatFragment = (ChatsFragment)getSupportFragmentManager().findFragmentByTag("Chat");
                        chatFragment.setContacts();
                    }

                    ChatUtilsMethods.createNewMessageNotification(getApplicationContext(),chat,receiveMessage);


                }
            });
        }
    };

}
