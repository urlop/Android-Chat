package com.github.nkzawa.socketio.androidchat.HomeView.Chats;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.androidchat.ChatApplication;
import com.github.nkzawa.socketio.androidchat.Constants;
import com.github.nkzawa.socketio.androidchat.HomeView.Chats.Groups.CreateGroupActivity;
import com.github.nkzawa.socketio.androidchat.Models.Chat;
import com.github.nkzawa.socketio.androidchat.PreferencesManager;
import com.github.nkzawa.socketio.androidchat.R;
import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class ChatsFragment extends Fragment {

    RecyclerView rv_friends;
    private Button btn_create_group;
    List<Chat> chatList = new ArrayList<>();
    private PreferencesManager mPreferences;
    private ChatAdapter chatAdapter;
    public Socket mSocket;

    public ChatsFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static ChatsFragment newInstance() {
        ChatsFragment fragment = new ChatsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPreferences = PreferencesManager.getInstance(getActivity());
        ChatApplication app = (ChatApplication)getActivity().getApplication();
        mSocket = app.getSocket();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chats, container, false);

        setupView(view);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("mlaalalla", "asdasdad");
        mSocket.on("typing", onTyping);
        mSocket.on("stop typing", onStopTyping);
    }

    @Override
    public void onPause() {
        super.onPause();

        mSocket.off("typing", onTyping);
        mSocket.off("stop typing", onStopTyping);
    }


    @Override
    public void onDestroy() {

        super.onDestroy();
    }

    public void setupView(View view){
        rv_friends = (RecyclerView)view.findViewById(R.id.rv_friends);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rv_friends.setLayoutManager(layoutManager);

        btn_create_group = (Button)view.findViewById(R.id.btn_create_group);

        btn_create_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CreateGroupActivity.class);
                startActivityForResult(intent, 1);
            }
        });

        setContacts();
    }

    private Emitter.Listener onTyping = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d("typing","aaaa"+args[0]);
                    JSONObject data = (JSONObject) args[0];
                    JsonParser jsonParser = new JsonParser();
                    JsonObject gsonObject = (JsonObject)jsonParser.parse(data.toString());
                    String messageTyping = "is typing";
                    Chat chat = null;

                    if(gsonObject.has("receiver_room")){
                        chat = Chat.getChat(gsonObject.get("receiver_room").getAsJsonObject().get("id").getAsInt(), Constants.ROOM_CHAT);
                        String userName= "";
                        if(gsonObject.has("sender")){
                            JsonObject jsonObjectSender = gsonObject.get("sender").getAsJsonObject();
                            userName = jsonObjectSender.get("name").getAsString();
                            messageTyping = userName + " is typing";
                        }
                    }else{
                        if(gsonObject.has("sender")){
                            JsonObject jsonObjectSender = gsonObject.get("sender").getAsJsonObject();
                            chat = Chat.getChat(jsonObjectSender.get("id").getAsInt(), Constants.USER_CHAT);
                        }
                    }

                    if(chat!=null){
                       chatAdapter.setTypingMessage(messageTyping);
                       chatAdapter.setTyping(chat,true);
                    }

                }
            });
        }
    };

    private Emitter.Listener onStopTyping = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    JsonParser jsonParser = new JsonParser();
                    JsonObject gsonObject = (JsonObject)jsonParser.parse(data.toString());
                    Chat chat = null;

                    if(gsonObject.has("receiver_room")){
                        chat = Chat.getChat(gsonObject.get("receiver_room").getAsJsonObject().get("id").getAsInt(), Constants.ROOM_CHAT);
                    }else{
                        if(gsonObject.has("sender")){
                            JsonObject jsonObjectSender = gsonObject.get("sender").getAsJsonObject();
                            chat = Chat.getChat(jsonObjectSender.get("id").getAsInt(), Constants.USER_CHAT);
                        }
                    }
                    if(chat!=null){
                        chatAdapter.setTyping(chat,false);
                    }
                }
            });
        }
    };

    public void setContacts(){
        chatList = Chat.listAll(Chat.class);
        chatAdapter = new ChatAdapter(this, chatList);
        rv_friends.setAdapter(chatAdapter);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

//        Bundle extras = data.getExtras();
//        int position = 0 ;
//        if(extras != null) {
//            position = extras.getInt("position", 0);
//        }
        chatList = Chat.listAll(Chat.class);
        chatAdapter = new ChatAdapter(this, chatList);
        rv_friends.setAdapter(chatAdapter);
    }


}
