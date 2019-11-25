package com.example.moodbook.ui.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.moodbook.MainActivity;
import com.example.moodbook.R;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;


/**
 * This activity handles login and registration
 */

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    protected DBAuth dbAuth;

    private Button loginButton;
    private Button registerButton;
    protected EditText email;
    protected EditText password;
    private ImageView profile;
    private TextView forgotPasswordLink;

    private static final String TAG = "EmailPassword";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //Stuck logging in? use the following line once to log out the cached session:
//        mAuth.getInstance().signOut();

        mAuth = FirebaseAuth.getInstance();
        dbAuth = new DBAuth(mAuth, FirebaseFirestore.getInstance());


        loginButton = findViewById(R.id.login);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        profile = findViewById(R.id.profile);

        forgotPasswordLink = findViewById(R.id.forgot_password);


        // Login is not not modularized because FireBase calls are asynchronous. Since they are asynchronous, we can't depend on results returned from methods until the onCompleteListener knows that the task is finished
        // LOGIN button
        loginButton = findViewById(R.id.login);
        loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (dbAuth.verifyEmail(email.getText().toString())){
                    if (dbAuth.verifyPass(password.getText().toString())){
                        mAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            FirebaseUser loginResult = mAuth.getCurrentUser();
                                            updateUI(loginResult);
                                        } else {
                                            Toast.makeText(LoginActivity.this, "Authentication failed",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

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

                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivityForResult(intent, 1);
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

        forgotPasswordLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class));
            }
    });

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
     * This method starts the mainactivity when the user is logged in
     * @param currentUser
     */
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

    // https://stackoverflow.com/questions/10407159/how-to-manage-startactivityforresult-on-android  - Nishant    used for activity results
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                FirebaseUser user = mAuth.getCurrentUser();
                updateUI(user);
            }
        }
    }
}
