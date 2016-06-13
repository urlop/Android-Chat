package com.github.nkzawa.socketio.androidchat.HomeView.Chats;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.nkzawa.socketio.androidchat.Chat.ChatActivity;
import com.github.nkzawa.socketio.androidchat.Constants;
import com.github.nkzawa.socketio.androidchat.HomeView.HomeActivity;
import com.github.nkzawa.socketio.androidchat.Models.Chat;
import com.github.nkzawa.socketio.androidchat.Models.Room;
import com.github.nkzawa.socketio.androidchat.Models.User;
import com.github.nkzawa.socketio.androidchat.R;

import java.util.List;

/**
 * Created by rubymobile on 3/05/16.
 */
public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Chat> mChats;
    private ChatsFragment context;
    private String typingMessage = "is typing";

    public ChatAdapter(ChatsFragment context, List<Chat> chats) {
        mChats  = chats;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_friend, parent, false);
                viewHolder = new UserViewHolder(v);
        return  viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        Chat chat = mChats.get(position);

        final UserViewHolder userViewHolder = (UserViewHolder)viewHolder;
        final Object receiver = chat.getReceiver();


        final Intent intent = new Intent(context.getActivity(), ChatActivity.class);
        if(User.class.isInstance(receiver)){
            final User user = (User)receiver;
            userViewHolder.setUsername(""+user.getName());
            intent.putExtra("receiverId", user.getUserId());
            intent.putExtra("typeChat", Constants.USER_CHAT);
            intent.putExtra("position",position);
        }else{
            final Room room = (Room)receiver;
            userViewHolder.setUsername(""+room.getName());
            intent.putExtra("receiverId", room.getRoomId());
            intent.putExtra("typeChat", Constants.ROOM_CHAT);
            intent.putExtra("position",position);
        }

        userViewHolder.ll_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivityForResult(intent, 1);
                ((HomeActivity)context.getActivity()).setOpenChatActivity(true);
            }
        });


        if(chat.isTyping()){
            userViewHolder.setTyping(typingMessage);
        }else{
            if (chat.getLastMessage() != null) {
                userViewHolder.setTyping(chat.getLastMessage());
            }else{
                userViewHolder.setTyping("");
            }

        }

    }

    @Override
    public int getItemCount() {
        return mChats.size();
    }


    public void setTyping(Chat chat, boolean isTyping){
        for(int i=0 ; i<mChats.size(); i++){
            if(chat.getId().equals(mChats.get(i).getId())){
                mChats.get(i).setTyping(isTyping);
                notifyItemChanged(i);
                break;
            }
        }
    }

    public void setTypingMessage(String typingMessage) {
        this.typingMessage = typingMessage;
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {
        private TextView mUsernameView, tv_last_message;
        private LinearLayout ll_user;

        public UserViewHolder(View itemView) {
            super(itemView);

            mUsernameView = (TextView) itemView.findViewById(R.id.username);
            tv_last_message = (TextView) itemView.findViewById(R.id.tv_last_message);
            ll_user = (LinearLayout) itemView.findViewById(R.id.ll_user);
        }

        public void setUsername(String username) {
            if (null == mUsernameView) return;
            mUsernameView.setText(username);
        }

        public void setTyping(String message) {
            if (null == tv_last_message) return;
            tv_last_message.setText(message);
        }

    }

}