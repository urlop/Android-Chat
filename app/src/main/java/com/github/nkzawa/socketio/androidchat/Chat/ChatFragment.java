package com.github.nkzawa.socketio.androidchat.Chat;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
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
import com.github.nkzawa.socketio.androidchat.LibraryCropperActivity;
import com.github.nkzawa.socketio.androidchat.Models.Chat;
import com.github.nkzawa.socketio.androidchat.Models.Message;
import com.github.nkzawa.socketio.androidchat.Models.User;
import com.github.nkzawa.socketio.androidchat.PreferencesManager;
import com.github.nkzawa.socketio.androidchat.R;
import com.github.nkzawa.socketio.androidchat.ChatUtilsMethods;
import com.github.nkzawa.socketio.androidchat.retrofit.RestClient;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;

import org.json.JSONObject;

import java.io.File;
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
    private ChatActivity chatActivity;
    private Chat currentChat;
    private Uri newImageUri;
    private int countCapturedImages;
    private RestClient restClient;
    private PreferencesManager mPreferences;

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
        countCapturedImages = 0;
        restClient = new RestClient();
        mPreferences = PreferencesManager.getInstance(getActivity());

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

        ImageButton iv_btn_image = (ImageButton) view.findViewById(R.id.iv_btn_image);
        iv_btn_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToGetPicture();
            }
        });


    }


    private void addLog(String message) {
        mMessages.add(new Message.Builder(Message.TYPE_LOG)
                .message(message).build());
        mAdapter.notifyItemInserted(mMessages.size() - 1);
        scrollToBottom();
    }


    protected void addMessage(Message message) {
        mMessages.add(message);
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

        Message message = new Message.Builder(Message.TYPE_MESSAGE).username(mUsername).message(messageToSend).build();
        message.setChat(currentChat);
        message.save();
        currentChat.setLastMessage(message.getUsername()+": "+message.getMessage());
        currentChat.save();

        addMessage(message);

        // perform the sending message attempt.
        Log.d("see enviaa", "tu :"+messageToSend + "-" + receiverId+ "-" + "user");


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
                        if(gsonObject.has("sender")){
                            JsonObject jsonObjectSender = gsonObject.get("sender").getAsJsonObject();
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
                        if(gsonObject.has("sender")){
                            JsonObject jsonObjectSender = gsonObject.get("sender").getAsJsonObject();
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

                    JsonObject userJsonObj = gsonObject.getAsJsonObject("sender");
                    User user = User.parseUser(userJsonObj);
                    String message = "";

                    if(gsonObject.has("content") && !gsonObject.get("content").isJsonNull()){
                        message = gsonObject.get("content").getAsString();
                    }

                    String fileUrl = null;
                    if(gsonObject.has("media_file") && !gsonObject.get("media_file").isJsonNull()){
                        fileUrl = gsonObject.get("media_file").getAsString();
                    }
                    String contentType = null;
                    if(gsonObject.has("media_file_content_type") && !gsonObject.get("media_file_content_type").isJsonNull()){
                        contentType = gsonObject.get("media_file_content_type").getAsString();
                    }

                    Chat chatReceiver = currentChat;

                    Message receiveMessage = new Message.Builder(Message.TYPE_MESSAGE).username(user.getName()).message(message).build();
                    receiveMessage.setFileType(contentType);
                    receiveMessage.setFileUrl(fileUrl);

                    if(ChatUtilsMethods.isUserInsideChat(gsonObject, currentChat)){
                        removeTyping(user.getName());
                        addMessage(receiveMessage);
                    }else{
                        chatReceiver = ChatUtilsMethods.getChatFromNewMessage(gsonObject);
                        Log.e("esssssss","esssss "+gsonObject);
                    }

                    chatReceiver.setLastMessage(receiveMessage.getUsername()+": "+receiveMessage.getMessage());
                    chatReceiver.save();
                    receiveMessage.setChat(chatReceiver);
                    receiveMessage.save();


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

    private void goToGetPicture(){
        Intent intent = new Intent(getActivity(), LibraryCropperActivity.class);
        intent.putExtra("count", countCapturedImages);
        countCapturedImages++;
        startActivityForResult(intent, 1);
    }

    @Override
    public void onActivityResult(int  requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                newImageUri = Uri.parse(data.getStringExtra("result"));

                if(currentChat.getChatType().equals(Constants.USER_CHAT)){
                    sendMessage(""+receiverId,null,newImageUri);
                }else{
                    sendMessage(null,""+receiverId,newImageUri);
                }
            }
        }
    }

    public void sendMessage(String receiverUserId, String receiverRoomId,Uri uri){

        TypedFile typedFile = new TypedFile("image/jpg", new File(uri.getPath()));
        Log.d("aaaaaa sender_id"," es: "+mPreferences.getUserId());
        Log.d("aaaaaa receiver_user_id"," es: "+receiverUserId);
        Log.d("aaaaaa receiver_room_id"," es: "+receiverRoomId);
        restClient.getWebservices().sendMessage(""+mPreferences.getUserId(),receiverUserId,receiverRoomId,typedFile,null, new Callback<JsonObject>() {
            @Override
            public void success(JsonObject jsonObject, Response response) {

                Log.d("envio","aaaaa");
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }




}

