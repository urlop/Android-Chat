package com.github.nkzawa.socketio.androidchat.Chat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.github.nkzawa.socketio.androidchat.Chat.Actions.AddFriendToGroupActivity;
import com.github.nkzawa.socketio.androidchat.R;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.emitter.Emitter;

/**
 * Created by rubymobile on 20/04/16.
 */
public class GroupChatFragment extends MainFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
        mSocket.on("message sent room", onMessageRoom);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSocket.off("message sent room", onMessageRoom);
    }



    private Emitter.Listener onMessageRoom = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {

            Log.d("llega rooom" ,""+args[0].toString());
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String username;
                    String message;
                    try {
                        username = data.getString("userId");
                        message = data.getString("message");
                    } catch (JSONException e) {
                        return;
                    }

                    removeTyping(username);
                    addMessage(username, message);
                }
            });
        }
    };
    @Override
    protected void attemptSend(){
        super.attemptSend();
        Log.d("aaaa","antes1 "+ receiverId);
        Log.d("aaaa","adespues2 "+ receiverId);
        mSocket.emit("send message room", messageToSend, receiverId);
    }


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

