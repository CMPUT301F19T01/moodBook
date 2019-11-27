package com.example.moodbook.ui.myFriends;

import android.content.DialogInterface;
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
import com.example.moodbook.UserListAdapter;
import com.example.moodbook.UserListFragment;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class MyFriendsFragment extends UserListFragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = super.onCreateView(inflater, container, savedInstanceState,
                R.layout.fragment_myfriends, R.id.friend_listView);

        return root;
    }
}

