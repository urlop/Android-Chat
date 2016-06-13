package com.github.nkzawa.socketio.androidchat.HomeView;

import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.androidchat.Chat.ChatFragment;
import com.github.nkzawa.socketio.androidchat.ChatApplication;
import com.github.nkzawa.socketio.androidchat.ChatUtilsMethods;
import com.github.nkzawa.socketio.androidchat.Constants;
import com.github.nkzawa.socketio.androidchat.HomeView.Chats.ChatsFragment;
import com.github.nkzawa.socketio.androidchat.HomeView.Contacts.ContactsFragment;
import com.github.nkzawa.socketio.androidchat.Models.Attachment;
import com.github.nkzawa.socketio.androidchat.Models.Chat;
import com.github.nkzawa.socketio.androidchat.Models.Message;
import com.github.nkzawa.socketio.androidchat.Models.Room;
import com.github.nkzawa.socketio.androidchat.PreferencesManager;
import com.github.nkzawa.socketio.androidchat.R;
import com.github.nkzawa.socketio.androidchat.Models.User;
import com.github.nkzawa.socketio.androidchat.retrofit.RestClient;
import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONObject;

import java.util.List;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class HomeActivity extends AppCompatActivity {


    private RestClient restClient;
    private PreferencesManager mPreferences;
    private FragmentTabHost mTabHost;
    private Socket mSocket;
    private ChatApplication app;
    private static Boolean check_running_mode;
    private boolean openChatActivity = false;

    public static Boolean getCheck_running_mode() {
        return check_running_mode;
    }
    public void setOpenChatActivity(boolean openChatActivity) {
        this.openChatActivity = openChatActivity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        check_running_mode = true;
        restClient = new RestClient();
        mPreferences = PreferencesManager.getInstance(this);
        app = (ChatApplication)getApplication();
        mSocket = app.getSocket();

        setupView();
        getUserInfo();

    }

    @Override
    public void onResume() {
        super.onResume();
        openChatActivity = false;
        check_running_mode = true;
        mSocket.once(Socket.EVENT_CONNECT, onUserIsConnected);
        mSocket.on("activate user", onUserIsActivated);
        mSocket.on("message sent", onMessageSent);

        if(!mSocket.connected()){
            mSocket.connect();
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        check_running_mode = false;
        mSocket.off(Socket.EVENT_CONNECT, onUserIsConnected);

        if(openChatActivity){
            mSocket.off("activate user", onUserIsActivated);
            mSocket.off("message sent", onMessageSent);
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("mejor","kjkaka");
        mSocket.off("activate user", onUserIsActivated);
        mSocket.off("message sent", onMessageSent);
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

                    Attachment attachment = new Attachment();
                    if(gsonObject.has("attachment") && !gsonObject.get("attachment").isJsonNull()){
                        Log.d("si tiene attachment", " jojojoj");
                        attachment = Attachment.parseAttachment(gsonObject.get("attachment").getAsJsonObject());
                    }
                    attachment.save();


                    Chat chat = null;
                    if(gsonObject.has("receiver_room_id") && !gsonObject.get("receiver_room_id").isJsonNull()){
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
                    receiveMessage.setAttachment(attachment);
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
