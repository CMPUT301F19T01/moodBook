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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * This activity handles login and registration
 * Citation: https://firebase.google.com/docs/auth/android/manage-users?authuser=0
 * https://stackoverflow.com/questions/43599638/firebase-signinwithemailandpassword-and-createuserwithemailandpassword-not-worki -Sagar Raut   used for mAuthListener
 */
//TODO: change buttons .isEnabled when appropriate
public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private Button loginButton;
    private Button registerButton;
    private EditText email;
    private EditText password;
    private static final String TAG = "EmailPassword";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginButton = findViewById(R.id.login);

        mAuth = FirebaseAuth.getInstance();

        email = findViewById(R.id.username);
        password = findViewById(R.id.password);

        // LOGIN button
        loginButton = findViewById(R.id.login);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });

        // REGISTER button
        registerButton = findViewById(R.id.register);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register(email.getText().toString(), password.getText().toString());
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

        //startActivity(new Intent(LoginActivityOld.this, MainActivity.class));
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

    /**
     * This method verifys that the email and password are filled out. Email is of type email, password is > 6 chars
     * @param email
     * @param password
     * @return
     */
    private Boolean verify(String email, String password){

        return true;
    }

    /**
     * This method decides how to update the ui based on the user's login status
     * @param currentUser
     */
    private void updateUI(FirebaseUser currentUser){
        if (currentUser != null){
            Log.d(TAG, "User logged in:starting mainactivity");
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else {
            // update text views, show error messages
            Log.d(TAG, "User not logged in:");
        }
    }

    /**
     * This method
     */
    private void login(){

    }

    /**
     * This method creates a new user in Firebase
     */
    private void register(String email, String password){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }
}
