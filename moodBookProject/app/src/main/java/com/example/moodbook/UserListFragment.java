package com.example.moodbook;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.example.moodbook.ui.followers.MyFollowersFragment;
import com.example.moodbook.ui.myFriends.MyFriendsFragment;
import com.example.moodbook.ui.profile.FriendProfileViewActivity;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Arrays;

public abstract class UserListFragment extends PageFragment implements DBCollectionListener {
    private static final String[] SUBCLASSES_NAMES = {
            MyFriendsFragment.class.getSimpleName(),
            MyFollowersFragment.class.getSimpleName()
    };
    protected String TAG;

    // connect to DB
    private DBFriend friendDB;

    protected ListView userListView;
    protected UserListAdapter userListAdapter;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState,
                             String fragmentName) {
        if(!Arrays.asList(SUBCLASSES_NAMES).contains(fragmentName)){
            throw new IllegalArgumentException(fragmentName+" is not a permitted subclass!");
        }
        this.TAG = fragmentName;

        // get id of layout
        int layoutId = fragmentName.equals(SUBCLASSES_NAMES[0]) ?
                R.layout.fragment_myfriends : R.layout.fragment_followers;
        // create root View
        View root = super.onCreateView(inflater, container, savedInstanceState, layoutId);

        // initialize DB connector
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        friendDB = new DBFriend(mAuth, getContext(), TAG);

        // get id of layout & listView
        int listViewId;
        final String listType;
        // for MyFriends: set friendListListener
        if(TAG.equals(SUBCLASSES_NAMES[0])) {
            friendDB.setFriendListListener(this);
            listViewId = R.id.friend_listView;
            listType = "friend";
        }
        // for MyFollowers: set followerListListener
        else {
            friendDB.setFollowersListListener(this);
            listViewId = R.id.followers_listView;
            listType = "follower";
        }

        // Set up recyclerView and adapter
        userListView = root.findViewById(listViewId);
        // get current user
        final MoodbookUser currentUser = new MoodbookUser(
                mAuth.getCurrentUser().getDisplayName(),
                mAuth.getCurrentUser().getUid());
        setupAdapter(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, final View view, final int pos, long id) {
                final MoodbookUser selectedUser = (MoodbookUser) userListAdapter.getItem(pos);
                new AlertDialog.Builder(getActivity())
                        .setTitle(selectedUser.getUsername())
                        .setMessage("Would you like to view this User's Profile or delete this User")
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // for MyFriends: remove friend
                                if(TAG.equals(SUBCLASSES_NAMES[0])) {
                                    friendDB.removeFriend(currentUser, selectedUser);
                                }
                                // for MyFollowers: remove friend
                                else {
                                    friendDB.removeFollower(currentUser, selectedUser);
                                }
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

        return root;
    }

    @Override
    public void beforeGettingList() {
        userListAdapter.clear();
    }

    @Override
    public void onGettingItem(Object item) {
        if(item instanceof MoodbookUser) {
            userListAdapter.add((MoodbookUser)item);
        }
    }

    @Deprecated
    @Override
    public void afterGettingList() {
        // Do nothing
    }

    private void setupAdapter(AdapterView.OnItemClickListener itemClickListener) {
        userListAdapter = new UserListAdapter(getContext());
        userListView.setAdapter(userListAdapter);
        // When a friend/follower item is clicked, opens dialog to ask user whether to remove the follower
        userListView.setOnItemClickListener(itemClickListener);
    }
}
