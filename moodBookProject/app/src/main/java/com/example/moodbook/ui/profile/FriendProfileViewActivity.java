package com.example.moodbook.ui.profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class FriendProfileViewActivity extends AppCompatActivity implements ProfileEditor.ProfileListener{
    ImageView friend_dp;
    TextView friend_username;
    TextView friend_email;
    TextView friend_phone;
    TextView friend_bio;
    private FirebaseAuth mAuth;
    ImageView emotion;
    Button close;
    String friend_uid;

    private FirebaseFirestore db;

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

        String intent_username = getIntent().getStringExtra("username");
        friend_uid = getIntent().getStringExtra("userID");

        friend_username.setText(intent_username);
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        storageReference.child("profilepics/" + intent_username + ".jpeg" ).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(getApplicationContext()/* context */)
                        .load(uri)
                        .centerCrop()
                        .into(friend_dp);
            }
        }) ;

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(FriendProfileViewActivity.this, MainActivity.class));

            }
        });

        ProfileEditor.getProfileData(friend_uid,this);
        /*final DocumentReference docRef = collectionReference.document(intent_userID);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("DocumentSnapshot data: ", "DocumentSnapshot data: " + document.get("phone"));
                        friend_phone.setText((CharSequence) document.get("phone"));
                        friend_bio.setText((CharSequence) document.get("bio"));
                        friend_email.setText((CharSequence) document.get("email"));
                        String recent_moodID = (String) document.get("recent_moodID");
                        if (recent_moodID != null) {
                            DocumentReference moodRef = docRef.collection("MOODS").document(recent_moodID);
                            moodRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> t) {
                                    DocumentSnapshot doc = t.getResult();
                                    if(doc.exists()){
                                        most_recent_mood= (String) doc.get("emotion");
                                        switch (most_recent_mood){
                                            case "sad":
                                                emotion.setImageResource(R.drawable.sad);
                                                break;
                                            case "happy":
                                                emotion.setImageResource(R.drawable.happy);
                                                break;
                                            case  "afraid":
                                                emotion.setImageResource(R.drawable.afraid);
                                                break;
                                            case "angry":
                                                emotion.setImageResource(R.drawable.angry);
                                                break;

                                        }
                                    }
                                }
                            });
                        }


                    } else {
                        Log.d("No such document", "No such document");
                    }
                } else {
                    Log.d("get failed with ", "get failed with ", task.getException());
                }
            }
        });*/

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
