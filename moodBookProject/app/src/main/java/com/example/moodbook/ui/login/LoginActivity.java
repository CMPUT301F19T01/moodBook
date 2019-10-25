package com.example.moodbook.ui.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.moodbook.MainActivity;
import com.example.moodbook.R;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

/**
 * This activity handles login and registration
 * Citation: https://firebase.google.com/docs/auth/android/manage-users?authuser=0
 * https://stackoverflow.com/questions/43599638/firebase-signinwithemailandpassword-and-createuserwithemailandpassword-not-worki -Sagar Raut   used for mAuthListener
 * https://stackoverflow.com/questions/16812039/how-to-check-valid-email-format-entered-in-edittext  - iversoncru   used for verifying email format
 */
//TODO:
//  BUG: toast message is shown as failing login/registration when actually succeeding
//  POSSIBLE BUG: two users attempt registering at the (sameish) time.. depends on when activity was created
//  currently, the list of all usernames is cached on creation of activity. this is used as a workaround. the firebase call to retrieve the documents in the usernamelist collection is done synchronously so it won't return in time if I update it as its needed
    // fix by having the activity halt until a response from firebase is recieved maybe?

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    protected DBAuth dbAuth;

    protected ArrayList<String> usernameList;

    private Button loginButton;
    private Button registerButton;
    protected EditText email;
    protected EditText password;

    private static final String TAG = "EmailPassword";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //Stuck logging in? use the following line once to log out the cached session:
        //mAuth.getInstance().signOut();

        mAuth = FirebaseAuth.getInstance();
        dbAuth = new DBAuth(mAuth, getApplicationContext());

        usernameList = dbAuth.getUsernameList(); //


        loginButton = findViewById(R.id.login);

        email = findViewById(R.id.username);
        password = findViewById(R.id.password);

        // LOGIN button
        loginButton = findViewById(R.id.login);
        loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (dbAuth.verifyEmail(email.getText().toString())){
                    if (dbAuth.verifyPass(password.getText().toString())){
                        FirebaseUser loginResult = dbAuth.login(email.getText().toString(), password.getText().toString());
                        if (loginResult != null){
                            updateUI(loginResult);
                        }
                        updateUI(null);
                        Toast.makeText(LoginActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        password.setError("Password must be >= 6 chars");
                    }
                } else {
                    email.setError("Incorrect email format");
                }
            }
        });

        // REGISTER button
        registerButton = findViewById(R.id.register);
        registerButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (dbAuth.verifyEmail(email.getText().toString())){
                    if (dbAuth.verifyPass(password.getText().toString())){
                        new UsernameFragment().show(getSupportFragmentManager(), "registering");
                    } else {
                        password.setError("Password must be >= 6 chars");
                    }
                } else {
                    email.setError("Incorrect email format");
                }
                //register(email.getText().toString(), password.getText().toString());
            }
        });

        // Auth listener checks if user is logged in
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                updateUI(user);
            }
        };

    }

    @Override
    public void onStart(){
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop(){
        super.onStop();
        if (mAuthListener != null){
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
    protected void updateUI(FirebaseUser currentUser){
        if (currentUser != null){
            Log.d(TAG, "User logged in:starting mainactivity");
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else {
            // update text views, show error messages
            Log.d(TAG, "User not logged in");
        }
    }
}
