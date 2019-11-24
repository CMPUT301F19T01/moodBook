package com.example.moodbook.ui.chat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.moodbook.DBFriend;
import com.example.moodbook.R;
import com.example.moodbook.ui.myFriends.MyFriendsFragment;
import com.google.firebase.auth.FirebaseAuth;

public class MessageActivity extends AppCompatActivity {
    private static final String TAG = MyFriendsFragment.class.getSimpleName();
    Intent intent;

    // connect to DB
    private DBFriend friendDB;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);


        // initialize DB connector
        mAuth = FirebaseAuth.getInstance();
        friendDB = new DBFriend(mAuth, getContext(), TAG);
    }
}
