package com.github.nkzawa.socketio.androidchat.Chat.Actions;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.nkzawa.socketio.androidchat.Models.User;
import com.github.nkzawa.socketio.androidchat.R;

import java.util.List;

/**
 * Created by rubymobile on 25/04/16.
 */
public class FriendsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<User> mContacts;

    public FriendsAdapter(List<User> contacts) {
        mContacts = contacts;}

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        View v = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_friend, parent, false);
        viewHolder = new UserViewHolder(v);

        return  viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
         UserViewHolder userViewHolder = (UserViewHolder)viewHolder;
         User user = mContacts.get(position);

        userViewHolder.setUsername(""+user.getName());

        userViewHolder.ll_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


    }

    @Override
    public int getItemCount() {
        return mContacts.size();
    }



    public class UserViewHolder extends RecyclerView.ViewHolder {
        private TextView mUsernameView;
        private LinearLayout ll_user;

        public UserViewHolder(View itemView) {
            super(itemView);

            mUsernameView = (TextView) itemView.findViewById(R.id.username);
            ll_user = (LinearLayout) itemView.findViewById(R.id.ll_user);
        }

        public void setUsername(String username) {
            if (null == mUsernameView) return;
            mUsernameView.setText(username);
        }

    }


}