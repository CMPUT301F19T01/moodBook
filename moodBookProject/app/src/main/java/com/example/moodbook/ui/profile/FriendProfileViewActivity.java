package com.example.moodbook.ui.profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.moodbook.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class FriendProfileViewActivity extends AppCompatActivity {
    ImageView friend_dp;
    TextView friend_username;
    TextView friend_email;
    TextView friend_phone;
    TextView friend_bio;
    Button message;
    Button unfriend;
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

        String intent_username = getIntent().getStringExtra("username");
        String intent_userID = getIntent().getStringExtra("userID");

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
        DocumentReference docRef = collectionReference.document(intent_userID);
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
                    } else {
                        Log.d("No such document", "No such document");
                    }
                } else {
                    Log.d("get failed with ", "get failed with ", task.getException());
                }
            }
        });




    }
}
