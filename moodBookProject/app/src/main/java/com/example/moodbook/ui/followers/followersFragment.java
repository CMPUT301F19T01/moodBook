package com.example.moodbook.ui.followers;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;

import com.example.moodbook.DBFriend;
import com.example.moodbook.PageFragment;
import com.example.moodbook.R;
import com.google.firebase.auth.FirebaseAuth;

public class followersFragment extends PageFragment {
//
     private followersAdapter followersListAdapter;
     private ListView followersListView;
    private static final String TAG = followersFragment.class.getSimpleName();

    // connect to DB
    private DBFriend friendDB;
    private FirebaseAuth mAuth;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = super.onCreateView(inflater, container, savedInstanceState, R.layout.fragment_followers);

        followersListView = root.findViewById(R.id.followers_listView);
        followersListAdapter=  new followersAdapter(getContext());
        followersListView.setAdapter(followersListAdapter);

        // initialize DB connector
        mAuth = FirebaseAuth.getInstance();
        friendDB = new DBFriend(mAuth, getContext(), TAG);
        friendDB.setFollowersListListener(followersListAdapter);

        return root;
    }
}