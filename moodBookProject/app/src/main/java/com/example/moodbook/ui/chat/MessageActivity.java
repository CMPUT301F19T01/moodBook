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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import static java.security.AccessController.getContext;

public class MessageActivity extends AppCompatActivity {
    private static final String TAG = MyFriendsFragment.class.getSimpleName();
    Intent intent;
    private FriendListAdapter friendListAdapter;
    private ArrayList<MoodbookUser> friends;
    TextView sendText, usernameTextView;
    ImageButton sendBtn;
    private String uid;
    private FirebaseFirestore db;
    private CollectionReference userReference = db.collection("USERS");

//    TextView friend_username;

    MessageAdapter messageAdapter;
    ArrayList<Chat> chat;
    RecyclerView message_recyclerView;

    // connect to DB
    private DBFriend messageDB;
    private DBFriend friendDB;

    private FirebaseAuth mAuth;
    FirebaseUser firebaseUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

//        // initialize DB connector
//        mAuth = FirebaseAuth.getInstance();
//        friendDB = new DBFriend(mAuth, getBaseContext(), TAG);
//        friendDB.setFriendListListener(friendListAdapter);

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
        messageAdapter = new MessageAdapter(MessageActivity.this, new ArrayList<Chat>());
        messageDB = new DBFriend(mAuth, getBaseContext(),TAG );
//        message_recyclerView.setMessageListener(messageAdapter);

//
//        public void readMessages(final String myUid, final String friendUid){
////            String myUid = mAuth.getCurrentUser().getUid();
//            chat = new ArrayList<>();
//            final CollectionReference friendsReference = this.userReference
//                    .document(friendUid).collection("CHAT");
//            friendsReference.
//
//        }


    }

}
