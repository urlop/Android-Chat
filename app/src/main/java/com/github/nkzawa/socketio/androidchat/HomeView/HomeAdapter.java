package com.github.nkzawa.socketio.androidchat.HomeView;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.nkzawa.socketio.androidchat.Constants;
import com.github.nkzawa.socketio.androidchat.Chat.MainActivity;
import com.github.nkzawa.socketio.androidchat.Models.Group;
import com.github.nkzawa.socketio.androidchat.R;
import com.github.nkzawa.socketio.androidchat.Models.User;

import java.util.List;
import java.util.Objects;

/**
 * Created by rubymobile on 19/04/16.
 */
public class HomeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int TYPE_FRIEND = 0;
    private final int TYPE_GROUP = 1;
    private List<Object> mContacts;
    private HomeActivity context;

    public HomeAdapter(HomeActivity context, List<Object> contacts) {
        mContacts = contacts;
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
                    intent.putExtra("receiverId", user.getId());
                    intent.putExtra("numUsers", context.numUsers);
                    intent.putExtra("typeChat", Constants.USER_CHAT);
                    context.startActivity(intent);
                    context.finish();
                }
            });


        }else{
            GroupViewHolder groupViewHolder = (GroupViewHolder)viewHolder;
            final Group group = (Group)mContacts.get(position);

            groupViewHolder.setGroupname(""+group.getName());

            groupViewHolder.ll_group.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, MainActivity.class);
                    intent.putExtra("receiverId", group.getId());
                    intent.putExtra("numUsers", context.numUsers);
                    intent.putExtra("typeChat", Constants.GROUP_CHAT);
                    context.startActivity(intent);
                    context.finish();
                }
            });
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

    public class GroupViewHolder extends RecyclerView.ViewHolder {
        private TextView mGroupView;
        private LinearLayout ll_group;

        public GroupViewHolder(View itemView) {
            super(itemView);

            mGroupView = (TextView) itemView.findViewById(R.id.groupname);
            ll_group = (LinearLayout) itemView.findViewById(R.id.ll_group);
        }

        public void setGroupname(String username) {
            if (null == mGroupView) return;
            mGroupView.setText(username);
        }

    }
}