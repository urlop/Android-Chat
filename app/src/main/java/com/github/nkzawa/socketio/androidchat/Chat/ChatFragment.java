package com.github.nkzawa.socketio.androidchat.Chat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.socketio.androidchat.Chat.Actions.AddFriendToGroupActivity;
import com.github.nkzawa.socketio.androidchat.ChatApplication;
import com.github.nkzawa.socketio.androidchat.Constants;
import com.github.nkzawa.socketio.androidchat.Models.Chat;
import com.github.nkzawa.socketio.androidchat.Models.Message;
import com.github.nkzawa.socketio.androidchat.Models.User;
import com.github.nkzawa.socketio.androidchat.PreferencesManager;
import com.github.nkzawa.socketio.androidchat.R;
import com.github.nkzawa.socketio.androidchat.retrofit.ChatUtilsMethods;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * A chat fragment containing messages view and input form.
 */
public class ChatFragment extends Fragment {

    private static final int REQUEST_LOGIN = 0;

    private static final int TYPING_TIMER_LENGTH = 600;

    private RecyclerView mMessagesView;
    private EditText mInputMessageView;
    private List<Message> mMessages = new ArrayList<>();
    private RecyclerView.Adapter mAdapter;
    private boolean mTyping = false;
    private Handler mTypingHandler = new Handler();
    private String mUsername;
    protected int receiverId;
    protected String messageToSend;
    public Socket mSocket;
    private PreferencesManager mPreferences;
    private ChatActivity chatActivity;
    private Chat currentChat;

    public ChatFragment() {
        super();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        if(((ChatActivity)getActivity()).getTypeChat().equals(Constants.ROOM_CHAT)){
            setHasOptionsMenu(true);
        }
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
        chatActivity = (ChatActivity) getActivity();
        mPreferences = PreferencesManager.getInstance(getActivity());
        ChatApplication app = (ChatApplication) getActivity().getApplication();
        mSocket = app.getSocket();
        mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        mSocket.on("typing", onTyping);
        mSocket.on("stop typing", onStopTyping);
        mSocket.on("message sent", onMessageSent);
        mUsername = mPreferences.getUserName();
        receiverId = ((ChatActivity)getActivity()).getReceiverId();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mSocket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.off(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        mSocket.off("typing", onTyping);
        mSocket.off("stop typing", onStopTyping);
        mSocket.off("message sent", onMessageSent);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mMessagesView = (RecyclerView) view.findViewById(R.id.messages);
        mMessagesView.setLayoutManager(new LinearLayoutManager(getActivity()));


        currentChat = Chat.createChat(receiverId,((ChatActivity)getActivity()).getTypeChat());
        if(currentChat != null){
            mMessages = currentChat.getMessages();
            Log.d("esss","miraaaame "+mMessages.size());
        }

        mAdapter = new MessageAdapter(chatActivity, mMessages);
        mMessagesView.setAdapter(mAdapter);

        mInputMessageView = (EditText) view.findViewById(R.id.message_input);
        mInputMessageView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int id, KeyEvent event) {
                if (id == R.id.send || id == EditorInfo.IME_NULL) {
                    attemptSend();
                    return true;
                }
                return false;
            }
        });
        mInputMessageView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (null == mUsername) return;
                if (!mSocket.connected()) return;

                if (!mTyping) {
                    mTyping = true;
                    if(chatActivity.getTypeChat().equals(Constants.USER_CHAT)){
                        mSocket.emit("typing",receiverId,"user");
                    }else{
                        mSocket.emit("typing",receiverId,"room");
                    }

                }

                mTypingHandler.removeCallbacks(onTypingTimeout);
                mTypingHandler.postDelayed(onTypingTimeout, TYPING_TIMER_LENGTH);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        ImageButton sendButton = (ImageButton) view.findViewById(R.id.send_button);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptSend();
            }
        });


    }


    private void addLog(String message) {
        mMessages.add(new Message.Builder(Message.TYPE_LOG)
                .message(message).build());
        mAdapter.notifyItemInserted(mMessages.size() - 1);
        scrollToBottom();
    }


    protected void addMessage(String username, String message) {
        mMessages.add(new Message.Builder(Message.TYPE_MESSAGE)
                .username(username).message(message).build());
        mAdapter.notifyItemInserted(mMessages.size() - 1);
        scrollToBottom();
    }

    private void addTyping(String username) {
        mMessages.add(new Message.Builder(Message.TYPE_ACTION)
                .username(username).build());
        mAdapter.notifyItemInserted(mMessages.size() - 1);
        scrollToBottom();
    }

    protected void removeTyping(String username) {
        for (int i = mMessages.size() - 1; i >= 0; i--) {
            Message message = mMessages.get(i);
            if (message.getType() == Message.TYPE_ACTION && message.getUsername().equals(username)) {
                mMessages.remove(i);
                mAdapter.notifyItemRemoved(i);
            }
        }
    }

    protected void attemptSend() {
        if (null == mUsername) return;
        if (!mSocket.connected()) return;

        mTyping = false;

        messageToSend = mInputMessageView.getText().toString().trim();
        if (TextUtils.isEmpty(messageToSend)) {
            mInputMessageView.requestFocus();
            return;
        }

        mInputMessageView.setText("");
        addMessage(mUsername, messageToSend);

        // perform the sending message attempt.
        Log.d("see enviaa", "tu :"+messageToSend + "-" + receiverId+ "-" + "user");

        Message message = new Message.Builder(Message.TYPE_MESSAGE).username(mUsername).message(messageToSend).build();
        message.setChat(currentChat);
        message.save();
        currentChat.setLastMessage(message.getUsername()+": "+message.getMessage());
        currentChat.save();

        mSocket.emit("send message", messageToSend, receiverId,((ChatActivity)getActivity()).getTypeChat());
    }



    private void scrollToBottom() {
        mMessagesView.scrollToPosition(mAdapter.getItemCount() - 1);
    }

    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getActivity().getApplicationContext(),
                            R.string.error_connect, Toast.LENGTH_LONG).show();
                }
            });
        }
    };


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

                    if(ChatUtilsMethods.isUserInsideChat(gsonObject, currentChat)){
                        if(gsonObject.has("user")){
                            JsonObject jsonObjectSender = gsonObject.get("user").getAsJsonObject();
                            User user = User.parseUser(jsonObjectSender);
                            addTyping(user.getName());
                        }
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

                    Log.d("typing","aaaa"+args[0]);
                    JSONObject data = (JSONObject) args[0];
                    JsonParser jsonParser = new JsonParser();
                    JsonObject gsonObject = (JsonObject)jsonParser.parse(data.toString());

                    if(ChatUtilsMethods.isUserInsideChat(gsonObject, currentChat)){
                        if(gsonObject.has("user")){
                            JsonObject jsonObjectSender = gsonObject.get("user").getAsJsonObject();
                            User user = User.parseUser(jsonObjectSender);
                            removeTyping(user.getName());
                        }
                    }
                }
            });
        }
    };



    private Runnable onTypingTimeout = new Runnable() {
        @Override
        public void run() {
            if (!mTyping) return;

            mTyping = false;
            if(chatActivity.getTypeChat().equals(Constants.USER_CHAT)){
                mSocket.emit("stop typing",receiverId,"user");
            }else{
                mSocket.emit("stop typing",receiverId,"room");
            }
        }
    };


    private Emitter.Listener onMessageSent = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {

            Log.d("aaaa","antes1 "+args[0]);
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    JsonParser jsonParser = new JsonParser();
                    JsonObject gsonObject = (JsonObject)jsonParser.parse(data.toString());

                    JsonObject userJsonObj = gsonObject.getAsJsonObject("user");
                    User user = User.parseUser(userJsonObj);
                    String message = gsonObject.get("message").getAsString();

                    Chat chatReceiver = currentChat;

                    if(ChatUtilsMethods.isUserInsideChat(gsonObject, currentChat)){
                        removeTyping(user.getName());
                        addMessage(user.getName(), message);
                    }else{
                        chatReceiver = ChatUtilsMethods.getChatFromNewMessage(gsonObject);
                        Log.e("esssssss","esssss "+gsonObject);
                    }

                    Message receiveMessage = new Message.Builder(Message.TYPE_MESSAGE).username(user.getName()).message(message).build();
                    receiveMessage.setChat(chatReceiver);
                    receiveMessage.save();
                    chatReceiver.setLastMessage(receiveMessage.getUsername()+": "+receiveMessage.getMessage());
                    chatReceiver.save();

                    if(currentChat != chatReceiver){
                        ChatUtilsMethods.createNewMessageNotification(getActivity(),chatReceiver, receiveMessage);

                        Log.e("esssssss","esssss "+gsonObject);

                    }

                }
            });
        }
    };




    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.chat_actions, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_add_friend) {
            Intent i = new Intent(getActivity(), AddFriendToGroupActivity.class);
            i.putExtra("groupId",receiverId);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}

