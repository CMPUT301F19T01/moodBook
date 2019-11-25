package com.example.moodbook.ui.followers;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.example.moodbook.DBFriend;
import com.example.moodbook.Mood;
import com.example.moodbook.MoodbookUser;
import com.example.moodbook.PageFragment;
import com.example.moodbook.R;
import com.google.firebase.auth.FirebaseAuth;

public class followersFragment extends PageFragment {

    private followersAdapter followersListAdapter;
    private ListView followersListView;
    private static final String TAG = followersFragment.class.getSimpleName();

    // connect to DB
    private DBFriend followersDB;
    private FirebaseAuth mAuth;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = super.onCreateView(inflater, container, savedInstanceState, R.layout.fragment_followers);

        followersListView = root.findViewById(R.id.followers_listView);
        followersListAdapter=  new followersAdapter(getContext());
        followersListView.setAdapter(followersListAdapter);

        // initialize DB connector
        mAuth = FirebaseAuth.getInstance();
        followersDB = new DBFriend(mAuth, getContext(), TAG);
        followersDB.setFollowersListListener(followersListAdapter);

        // get current user with username and uid
        final MoodbookUser currentUser = new MoodbookUser(
                mAuth.getCurrentUser().getDisplayName(),
                mAuth.getCurrentUser().getUid());

        // When a follower item is clicked, opens dialog to ask user whether to remove the follower
        followersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, final View view, final int pos, long id) {
                new AlertDialog.Builder(getActivity())
                        .setTitle("Remove User")
                        .setMessage("Do you want to remove this user from your follower list?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                MoodbookUser selectedUser = (MoodbookUser) followersListAdapter.getItem(pos);
                                //followersDB.removeFollower(selectedUser, username, selectedUser.getUsername());
                                followersDB.removeFollower(currentUser, selectedUser);
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });

        return root;
    }
}