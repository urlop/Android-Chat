package com.github.nkzawa.socketio.androidchat.HomeView;

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
import android.widget.Toast;

import com.github.nkzawa.socketio.androidchat.HomeView.Groups.CreateGroupActivity;
import com.github.nkzawa.socketio.androidchat.Models.Room;
import com.github.nkzawa.socketio.androidchat.Models.User;
import com.github.nkzawa.socketio.androidchat.PreferencesManager;
import com.github.nkzawa.socketio.androidchat.R;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class ContactsFragment extends Fragment {

    RecyclerView rv_friends;
    List<Object> contactsList = new ArrayList<>();
    Button btn_create_group;
    public Socket mSocket;
    private PreferencesManager mPreferences;
    private HomeAdapter homeAdapter;

    public ContactsFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static ContactsFragment newInstance() {
        ContactsFragment fragment = new ContactsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);
        setupView(view);
        return view;
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
                startActivity(intent);
            }
        });

        setContacts();
    }


    private void setContacts(){
        List<User> friends = User.listAll(User.class);
        List<Room> rooms = Room.listAll(Room.class);

        homeAdapter = new HomeAdapter(getActivity(), friends, rooms);
        rv_friends.setAdapter(homeAdapter);
    }

}
