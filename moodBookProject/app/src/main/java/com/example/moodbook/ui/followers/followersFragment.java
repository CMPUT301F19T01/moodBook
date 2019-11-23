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
import com.example.moodbook.MoodbookUser;
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

        followersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, final View view, final int pos, long id) {
                final int row = pos;
                new AlertDialog.Builder(getActivity())
                        .setTitle("Remove User")
                        .setMessage("Are you sure you want to remove this user in your follower list?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                MoodbookUser selectedUser = (MoodbookUser) followersListView.getItemAtPosition(pos);
                                String followerUser = selectedUser.getUsername();
                                Toast.makeText(getContext(), followerUser, Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });
        return root;
    }
}