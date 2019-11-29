package com.example.moodbook.ui.profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.moodbook.Mood;
import com.example.moodbook.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

/**
 * This class displays the profile page For friends
 * @see EditProfileActivity
 * @see ProfileEditor
 */

public class ProfileViewActivity extends AppCompatActivity implements ProfileEditor.ProfileListener{
    ImageView user_dp;
    TextView user_username;
    TextView user_email;
    TextView user_phone;
    TextView user_bio;
    private FirebaseAuth mAuth;
    ImageView emotion;
    FloatingActionButton edit_button;
    Button close;
    String user_uid;
    String user;


    private FirebaseFirestore db;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_view);
        db = FirebaseFirestore.getInstance();
        CollectionReference collectionReference = db.collection("USERS");

        //Initialize views
        user_dp = findViewById(R.id.user_dp);
        user_username = findViewById(R.id.user_username);
        user_email = findViewById(R.id.user_email);
        user_phone = findViewById(R.id.user_phone);
        user_bio = findViewById(R.id.user_bio);
        close = findViewById(R.id.close);
        emotion = findViewById(R.id.most_recent_mood);
        edit_button = findViewById(R.id.edit_profile);
        final String intent_username = getIntent().getStringExtra("username");
        final String intent_email =getIntent().getStringExtra("email");
        user_uid = getIntent().getStringExtra("userID");
        user = getIntent().getStringExtra("user");


        /**
         * For If the user is trying to look ar their own profile page
         */
        if(user != null){
            edit_button.setVisibility(View.VISIBLE);
            user_email.setText(intent_email);
            edit_button.setOnClickListener(new View.OnClickListener() {
                                               @Override
                                               public void onClick(View view) {
                                                   Intent intent = new Intent(ProfileViewActivity.this, EditProfileActivity.class);
                                                   intent.putExtra("username",intent_username);
                                                   intent.putExtra("userID", user_uid);
                                                   intent.putExtra("email",intent_email);
                                                   startActivity(intent);
                                               }}); }
        user_username.setText(intent_username);

        // Placing Profile picture
        StorageReference sRef = FirebaseStorage.getInstance().getReference().child("profilepics/" + intent_username + ".jpeg" );
        try{
            final File localFile = File.createTempFile("Images", "jpeg");
            sRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Bitmap obtainedImg = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                    user_dp.setImageBitmap(obtainedImg);
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
        ProfileEditor.getProfileData(user_uid,this);
    }

    /**
     * This overridden method gets the users data from the Firebase database
     * @param document
     *  A snapshot of the firebase database
     */
    @Override
    public void onGettingUserDoc(DocumentSnapshot document) {
        Log.d("DocumentSnapshot data: ", "DocumentSnapshot data: " + document.get("phone"));
        user_phone.setText((CharSequence) document.get("phone"));
        user_bio.setText((CharSequence) document.get("bio"));
        user_email.setText((CharSequence) document.get("email"));
        String recent_moodID = (String) document.get("recent_moodID");
        if (recent_moodID != null) {
            ProfileEditor.getMoodData(user_uid, recent_moodID, this);
        }
    }

    /**
     * This overridden method gets the users emotion from the Firebase database
     * @param document
     *  A snapshot of the firebase database
     */

    @Override
    public void onGettingMoodDoc(DocumentSnapshot document) {
        String most_recent_mood= (String) document.get("emotion");
        emotion.setImageResource(Mood.Emotion.getImageResourceId(most_recent_mood));
    }
}
