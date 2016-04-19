package com.github.nkzawa.socketio.androidchat;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by rubymobile on 19/04/16.
 */
public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.ViewHolder> {

    private final int TYPE_FRIEND = 0;
    private List<User> mUsers;
    private FriendsActivity context;

    public FriendsAdapter(FriendsActivity context, List<User> users) {
        mUsers = users;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layout = -1;
        switch (viewType) {
            case TYPE_FRIEND:
                layout = R.layout.item_friend;
                break;
        }
        View v = LayoutInflater
                .from(parent.getContext())
                .inflate(layout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        final User user = mUsers.get(position);
        viewHolder.setUsername(""+user.getId());

        viewHolder.ll_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra("username", user.getId());
                intent.putExtra("numUsers", context.numUsers);
                context.startActivity(intent);
                context.finish();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    @Override
    public int getItemViewType(int position) {
        return TYPE_FRIEND;

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mUsernameView;
        private LinearLayout ll_user;

        public ViewHolder(View itemView) {
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