package com.example.moodbook.ui.myFriends;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.moodbook.DBFriend;
import com.example.moodbook.MoodbookUser;
import com.example.moodbook.PageFragment;
import com.example.moodbook.R;
import com.example.moodbook.ui.chat.MessageActivity;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class MyFriendsFragment extends PageFragment {
    private FriendListAdapter friendListAdapter;
    private ListView friendListView;
    private static final String TAG = MyFriendsFragment.class.getSimpleName();
    Intent intent;

    // connect to DB
    private DBFriend friendDB;
    private FirebaseAuth mAuth;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = super.onCreateView(inflater, container, savedInstanceState, R.layout.fragment_myfriends);

        friendListView = root.findViewById(R.id.friend_listView);
        friendListAdapter=  new FriendListAdapter(getContext());
        friendListView.setAdapter(friendListAdapter);

        // initialize DB connector
        mAuth = FirebaseAuth.getInstance();
        friendDB = new DBFriend(mAuth, getContext(), TAG);
        friendDB.setFriendListListener(friendListAdapter);

        friendListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getContext(), "Clicked " , Toast.LENGTH_LONG).show();
                MoodbookUser friendId = (MoodbookUser)friendListAdapter.getItem(i);

                Intent intent = new Intent(getContext(),MessageActivity.class);
                MoodbookUser friendUser = (MoodbookUser)friendListAdapter.getItem(i);
                intent.putExtra("friend_uid", friendUser.getUid());
                intent.putExtra("friend_username", friendUser.getUsername());
                getContext().startActivity(intent);
            }
        });




        return root;
    }
}

