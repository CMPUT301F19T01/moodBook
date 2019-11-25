package com.example.moodbook.ui.chat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.moodbook.DBFriend;
import com.example.moodbook.MoodbookUser;
import com.example.moodbook.R;
import com.example.moodbook.ui.Request.RequestHandler;
import com.example.moodbook.ui.myFriends.FriendListAdapter;
import com.example.moodbook.ui.myFriends.MyFriendsFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class MessageActivity extends AppCompatActivity {
    private static final String TAG = MyFriendsFragment.class.getSimpleName();
    Intent intent;
    private FriendListAdapter friendListAdapter;
    private ArrayList<MoodbookUser> friends;
    TextView sendText, usernameTextView;
    ImageButton sendBtn;
//    TextView friend_username;

    MessageAdapter messageAdapter;
    ArrayList<Chat> chat;
    RecyclerView message_recyclerView;

    // connect to DB
    private DBFriend friendDB;
    private FirebaseAuth mAuth;
    FirebaseUser firebaseUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        // initialize DB connector
        mAuth = FirebaseAuth.getInstance();
        friendDB = new DBFriend(mAuth, getBaseContext(), TAG);
        friendDB.setFriendListListener(friendListAdapter);

        usernameTextView = findViewById(R.id.message_username);
        sendText= findViewById(R.id.message_text);
        sendBtn= findViewById(R.id.message_button);
        intent = getIntent();

        final RequestHandler requestHandler = new RequestHandler(mAuth,getBaseContext());
        final FirebaseUser username = mAuth.getCurrentUser();

        message_recyclerView = findViewById(R.id.message_recyclerView);
        message_recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        message_recyclerView.setLayoutManager(linearLayoutManager);

        final String friendUid = intent.getStringExtra("friend_uid");
        String friendUsername = intent.getStringExtra("friend_username");

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               String message = sendText.getText().toString();
                if (!message.equals("")){

                    requestHandler.sendMessage(username.getUid(), friendUid, message);
                }
                else {
                    Toast.makeText(MessageActivity.this,
                            "Cannot send an empty message",
                            Toast.LENGTH_LONG).show();
                }
                sendText.setText("");

            }
        });





    }
//    private void sendMessage(String sender, String receiver, String message){
//        String uid = mAuth.getCurrentUser().getUid();
//        final CollectionReference collectionReference = ;
//        HashMap<String, Object> data = new HashMap<>();
//        data.put("sender",sender);
//        data.put("receiver",receiver);
//        data.put("message",message);
}
