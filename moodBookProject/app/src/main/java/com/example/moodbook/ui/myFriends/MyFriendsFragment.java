package com.example.moodbook.ui.myFriends;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;

import com.example.moodbook.DBFriend;
import com.example.moodbook.DBListListener;
import com.example.moodbook.MoodbookUser;
import com.example.moodbook.PageFragment;
import com.example.moodbook.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class MyFriendsFragment extends PageFragment implements DBListListener {
    private FriendListAdapter friendListAdapter;
    private ListView friendListView;
    private static final String TAG = MyFriendsFragment.class.getSimpleName();

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
        friendDB.setFriendListListener(this);

        return root;
    }

    @Override
    public void beforeGettingList() {
        friendListAdapter.clear();
    }

    @Override
    public void onGettingItem(Object item) {
        if(item instanceof MoodbookUser) {
            friendListAdapter.add((MoodbookUser)item);
        }
    }

    @Deprecated
    @Override
    public void afterGettingList() {
        // Do nothing
    }
}

