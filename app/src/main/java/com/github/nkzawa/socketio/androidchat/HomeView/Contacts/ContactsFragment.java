package com.github.nkzawa.socketio.androidchat.HomeView.Contacts;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.github.nkzawa.socketio.androidchat.HomeView.Chats.ChatAdapter;
import com.github.nkzawa.socketio.androidchat.HomeView.Chats.Groups.CreateGroupActivity;
import com.github.nkzawa.socketio.androidchat.Models.Chat;
import com.github.nkzawa.socketio.androidchat.Models.User;
import com.github.nkzawa.socketio.androidchat.R;

import java.util.ArrayList;
import java.util.List;

public class ContactsFragment extends Fragment {

    RecyclerView rv_friends;
    List<User> contactsList = new ArrayList<>();
    private ContactsAdapter contactsAdapter;
    private Button btn_add_contact;

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
        btn_add_contact = (Button) view.findViewById(R.id.btn_add_contact);

        btn_add_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddContactActivity.class);
                startActivityForResult(intent, 1);
            }
        });

        setContacts();
    }


    public void setContacts(){
        contactsList = User.listAll(User.class);

        contactsAdapter = new ContactsAdapter(getActivity(), contactsList);
        rv_friends.setAdapter(contactsAdapter);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        contactsList = User.listAll(User.class);
        contactsAdapter = new ContactsAdapter(getActivity(), contactsList);
        rv_friends.setAdapter(contactsAdapter);
    }
}
