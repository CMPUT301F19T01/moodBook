package com.example.moodbook.ui.login;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.moodbook.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * This activity handles login and registration
 */
public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        //startActivity(new Intent(LoginActivityOld.this, MainActivity.class));
    }

    @Override
    public void onStart(){
        super.onStart();
        // Check if user is signed in
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null){
            finish();
        }
    }
}
