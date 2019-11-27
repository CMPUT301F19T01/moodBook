package com.example.moodbook;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class UserListFragment extends PageFragment implements DBListListener {
    protected UserListAdapter userListAdapter;
    protected ListView userListView;
    protected String TAG = UserListFragment.class.getSimpleName();
    ArrayList<MoodbookUser> userList;
    // connect to DB
    private DBFriend friendDB;
    private FirebaseAuth mAuth;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState,
                             int layoutId, int listViewId) {
        View root = super.onCreateView(inflater, container, savedInstanceState, layoutId);

        userList = new ArrayList<>();
        userListView = root.findViewById(listViewId);
        userListAdapter=  new UserListAdapter(getContext());
        userListView.setAdapter(userListAdapter);

        // initialize DB connector
        mAuth = FirebaseAuth.getInstance();
        friendDB = new DBFriend(mAuth, getContext(), TAG);
        friendDB.setFriendListListener(this);

        final MoodbookUser currentUser = new MoodbookUser(
                mAuth.getCurrentUser().getDisplayName(),
                mAuth.getCurrentUser().getUid());

        // When a friend item is clicked, opens dialog to ask user whether to remove the follower
        userListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, final View view, final int pos, long id) {
                final int row = pos;
                new AlertDialog.Builder(getActivity())
                        .setTitle("Remove User")
                        .setMessage("Do you want to remove this user from your friend list?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                MoodbookUser selectedUser = (MoodbookUser) userListAdapter.getItem(pos);
                                friendDB.removeFriend(currentUser, selectedUser);
                            }
                        })
                        .setNegativeButton("No", null)
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
}
