package com.example.moodbook;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

import com.example.moodbook.ui.profile.ProfilePicSetter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfilePage extends AppCompatActivity {
    private ProfilePicSetter DBpic;
    private FirebaseFirestore db;
    private String name;
    private String email;
    private ImageView dp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);
        DBpic = new ProfilePicSetter(getApplicationContext());
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            name = user.getDisplayName();
            email = user.getEmail();
//            Uri photoUrl = user.getPhotoUrl();

            boolean emailVerified = user.isEmailVerified();

            String uid = user.getUid();
        }
        dp= findViewById(R.id.dp);
        DBpic.getImageFromDB(name,dp);

    }
}
