package com.example.moodbook.ui.profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.moodbook.MainActivity;
import com.example.moodbook.Mood;
import com.example.moodbook.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

public class FriendProfileViewActivity extends AppCompatActivity implements ProfileEditor.ProfileListener{
    ImageView friend_dp;
    TextView friend_username;
    TextView friend_email;
    TextView friend_phone;
    TextView friend_bio;
    private FirebaseAuth mAuth;
    ImageView emotion;
    FloatingActionButton edit_button;
    Button close;
    String friend_uid;
    String user;
    String most_recent_mood;


    private FirebaseFirestore db;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_profile_view);
        db = FirebaseFirestore.getInstance();
        CollectionReference collectionReference = db.collection("USERS");
        friend_dp = (ImageView) findViewById(R.id.friend_dp);
        friend_username = (TextView)findViewById(R.id.friend_username);
        friend_email = (TextView)findViewById(R.id.friend_email);
        friend_phone = (TextView)findViewById(R.id.friend_phone);
        friend_bio = (TextView)findViewById(R.id.friend_bio);
        close = (Button) findViewById(R.id.close);
        emotion = findViewById(R.id.most_recent_mood);
        edit_button = findViewById(R.id.edit_profile);

        final String intent_username = getIntent().getStringExtra("username");
        final String intent_email =getIntent().getStringExtra("email");
        friend_uid = getIntent().getStringExtra("userID");
        user = getIntent().getStringExtra("user");


        if(user != null){
            edit_button.setVisibility(View.VISIBLE);
            friend_email.setText(intent_email);
            edit_button.setOnClickListener(new View.OnClickListener() {
                                               @Override
                                               public void onClick(View view) {
                                                   Intent intent = new Intent(FriendProfileViewActivity.this, ProfileActivity.class);
                                                   intent.putExtra("username",intent_username);
                                                   intent.putExtra("userID",friend_uid);
                                                   intent.putExtra("email",intent_email);
                                                   startActivity(intent);

                                               }
                                           }
            );

        }
        friend_username.setText(intent_username);
        StorageReference sRef = FirebaseStorage.getInstance().getReference().child("profilepics/" + intent_username + ".jpeg" );
        try{
            final File localFile = File.createTempFile("Images", "jpeg");
            sRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Bitmap obtainedImg = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                    friend_dp.setImageBitmap(obtainedImg);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();

            }
        });

        ProfileEditor.getProfileData(friend_uid,this);
    }

    @Override
    public void onGettingUserDoc(DocumentSnapshot document) {
        Log.d("DocumentSnapshot data: ", "DocumentSnapshot data: " + document.get("phone"));
        friend_phone.setText((CharSequence) document.get("phone"));
        friend_bio.setText((CharSequence) document.get("bio"));
        friend_email.setText((CharSequence) document.get("email"));
        String recent_moodID = (String) document.get("recent_moodID");
        if (recent_moodID != null) {
            ProfileEditor.getMoodData(friend_uid, recent_moodID, this);
        }
    }

    @Override
    public void onGettingMoodDoc(DocumentSnapshot document) {
        String most_recent_mood= (String) document.get("emotion");
        emotion.setImageResource(Mood.Emotion.getImageResourceId(most_recent_mood));
    }
}
