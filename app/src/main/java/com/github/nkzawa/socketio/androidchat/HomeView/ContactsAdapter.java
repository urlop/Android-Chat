package com.github.nkzawa.socketio.androidchat.HomeView;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.nkzawa.socketio.androidchat.Chat.ChatActivity;
import com.github.nkzawa.socketio.androidchat.Constants;
import com.github.nkzawa.socketio.androidchat.R;
import com.github.nkzawa.socketio.androidchat.Models.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rubymobile on 19/04/16.
 */
public class ContactsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int TYPE_FRIEND = 0;
    private final int TYPE_GROUP = 1;
    private List<Object> mContacts;
    private List<User> mUsers;
    private Activity context;

    public ContactsAdapter(Activity context, List<User> users) {
        mUsers  = users;
        mContacts = new ArrayList<>();
        mContacts.addAll(users);
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        View v  = LayoutInflater
                        .from(parent.getContext())
                        .inflate(R.layout.item_friend, parent, false);
                viewHolder = new UserViewHolder(v);
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
                    Intent intent = new Intent(context, ChatActivity.class);
                    intent.putExtra("receiverId", user.getUserId());
                    intent.putExtra("typeChat", Constants.USER_CHAT);
                    context.startActivity(intent);
                }
            });

        }

    }

    @Override
    public int getItemCount() {
        return mContacts.size();
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