package com.example.moodbook.ui.myFriends;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.example.moodbook.DBFriend;
import com.example.moodbook.DBListListener;
import com.example.moodbook.MoodbookUser;
import com.example.moodbook.PageFragment;
import com.example.moodbook.R;
import com.example.moodbook.ui.profile.FriendProfileViewActivity;
import com.example.moodbook.UserListAdapter;
import com.example.moodbook.UserListFragment;
import com.example.moodbook.ui.followers.MyFollowersFragment;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class MyFriendsFragment extends UserListFragment {

    private static final String TAG = MyFriendsFragment.class.getSimpleName();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = super.onCreateView(inflater, container, savedInstanceState, R.layout.fragment_myfriends);

        friendsList = new ArrayList<>();
        friendListView = root.findViewById(R.id.friend_listView);
        friendListAdapter=  new FriendListAdapter(getContext());
        friendListView.setAdapter(friendListAdapter);

        // initialize DB connector
        mAuth = FirebaseAuth.getInstance();
        friendDB = new DBFriend(mAuth, getContext(), TAG);
        friendDB.setFriendListListener(this);

        final MoodbookUser currentUser = new MoodbookUser(
                mAuth.getCurrentUser().getDisplayName(),
                mAuth.getCurrentUser().getUid());

        // When a friend item is clicked, opens dialog to ask user whether to remove the follower
        friendListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, final View view, final int pos, long id) {
                final int row = pos;
                final MoodbookUser selectedUser = (MoodbookUser) friendListAdapter.getItem(pos);
                new AlertDialog.Builder(getActivity())
                        .setTitle(selectedUser.getUsername())
                        .setMessage("Would you like to view this User's Profile or delete this User")
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                friendDB.removeFriend(currentUser, selectedUser);
                            }
                        })
                        .setNegativeButton("View", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent viewProfile = new Intent(getContext(), FriendProfileViewActivity.class);
                                viewProfile.putExtra("username",selectedUser.getUsername());
                                viewProfile.putExtra("userID",selectedUser.getUid());
                                startActivity(viewProfile);
                            }
                        })
                        .setNeutralButton("Cancel",null)
                        .show();
            }
        });
        //View root = super.onCreateView(inflater, container, savedInstanceState, TAG);

        return root;
    }
}

