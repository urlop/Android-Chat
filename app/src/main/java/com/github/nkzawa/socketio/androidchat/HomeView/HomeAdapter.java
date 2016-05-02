package com.github.nkzawa.socketio.androidchat.HomeView;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.nkzawa.socketio.androidchat.Constants;
import com.github.nkzawa.socketio.androidchat.Chat.MainActivity;
import com.github.nkzawa.socketio.androidchat.Models.Room;
import com.github.nkzawa.socketio.androidchat.R;
import com.github.nkzawa.socketio.androidchat.Models.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rubymobile on 19/04/16.
 */
public class HomeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int TYPE_FRIEND = 0;
    private final int TYPE_GROUP = 1;
    private List<Object> mContacts;
    private List<User> mUsers;
    private List<Room> mRooms;
    private Activity context;
    private String typingMessage = "is typing";

    public HomeAdapter(Activity context, List<User> users, List<Room> rooms) {
        mUsers  = users;
        mRooms = rooms;
        mContacts = new ArrayList<>();
        mContacts.addAll(users);
        mContacts.addAll(rooms);
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        View v = null;

        switch (viewType) {
            case TYPE_FRIEND:
                v = LayoutInflater
                        .from(parent.getContext())
                        .inflate(R.layout.item_friend, parent, false);
                viewHolder = new UserViewHolder(v);
            break;

            case TYPE_GROUP:
                v = LayoutInflater
                        .from(parent.getContext())
                        .inflate(R.layout.item_group, parent, false);
                viewHolder = new GroupViewHolder(v);
            break;
        }

        return  viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        Object object = mContacts.get(position);

        if(UserViewHolder.class.isInstance(viewHolder)){
            UserViewHolder userViewHolder = (UserViewHolder)viewHolder;
            final User user = (User)mContacts.get(position);

            userViewHolder.setUsername(""+user.getName());

            userViewHolder.ll_user.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, MainActivity.class);
                    intent.putExtra("receiverId", user.getUserId());
                    intent.putExtra("typeChat", Constants.USER_CHAT);
                    context.startActivity(intent);
                }
            });


            if(user.isTyping()){
                userViewHolder.setTyping(typingMessage);
            }else{
                userViewHolder.setTyping("");
            }

        }else{
            GroupViewHolder groupViewHolder = (GroupViewHolder)viewHolder;
            final Room room = (Room)mContacts.get(position);

            groupViewHolder.setGroupname(""+ room.getName());

            groupViewHolder.ll_group.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, MainActivity.class);
                    intent.putExtra("receiverId", room.getRoomId());
                    intent.putExtra("typeChat", Constants.GROUP_CHAT);
                    context.startActivity(intent);
                }
            });


            if(room.isTyping()){
                groupViewHolder.setTyping(typingMessage);
            }else{
                groupViewHolder.setTyping("");
            }
        }


    }

    @Override
    public int getItemCount() {
        return mContacts.size();
    }

    @Override
    public int getItemViewType(int position) {

        Object object = mContacts.get(position);

        if(User.class.isInstance(object)){
            return TYPE_FRIEND;
        }else{
            return TYPE_GROUP;
        }


    }

    public void setTyping(Object object, boolean isTyping){
        int itemPosition = 0;

        if(User.class.isInstance(object)){
            for(int i=0 ; i<mUsers.size()-1; i++){
                if(mUsers.get(i).getUserId() == ((User)object).getUserId()){
                    itemPosition = i;
                    ((User)mContacts.get(itemPosition)).setTyping(isTyping);
                    break;
                }
            }
        }else{
            for(int i=0 ; i<mRooms.size()-1; i++){
                if(mRooms.get(i).getRoomId() == ((Room)object).getRoomId()){
                    itemPosition = mUsers.size() + i;
                    ((Room)mContacts.get(itemPosition)).setTyping(isTyping);
                    break;
                }
            }
        }


        notifyItemChanged(itemPosition);
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

    public class GroupViewHolder extends RecyclerView.ViewHolder {
        private TextView mGroupView, tv_last_message;
        private LinearLayout ll_group;

        public GroupViewHolder(View itemView) {
            super(itemView);

            tv_last_message = (TextView) itemView.findViewById(R.id.tv_last_message);
            mGroupView = (TextView) itemView.findViewById(R.id.groupname);
            ll_group = (LinearLayout) itemView.findViewById(R.id.ll_group);
        }

        public void setGroupname(String username) {
            if (null == mGroupView) return;
            mGroupView.setText(username);
        }

        public void setTyping(String message) {
            if (null == tv_last_message) return;
            tv_last_message.setText(message);
        }

    }
}