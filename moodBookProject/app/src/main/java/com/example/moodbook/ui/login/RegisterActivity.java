package com.example.moodbook.ui.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.moodbook.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

/**
 * This activity handles registration
 * Citation: https://firebase.google.com/docs/auth/android/manage-users?authuser=0
 * https://stackoverflow.com/questions/43599638/firebase-signinwithemailandpassword-and-createuserwithemailandpassword-not-worki -Sagar Raut   used for mAuthListener
 * https://stackoverflow.com/questions/16812039/how-to-check-valid-email-format-entered-in-edittext  - iversoncru   used for verifying email format
 * https://stackoverflow.com/questions/34110565/how-to-add-back-button-on-actionbar-in-android-studio  - adnbsr    used for back button
 */
//TODO:
//  BUG: toast message is shown as failing login/registration when actually succeeding
//  POSSIBLE BUG: two users attempt registering at the (sameish) time.. depends on when activity was created
//  currently, the list of all usernames is cached on creation of activity. this is used as a workaround. the firebase call to retrieve the documents in the usernamelist collection is done synchronously so it won't return in time if I update it as its needed
    // fix by having the activity halt until a response from firebase is recieved maybe?

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    protected DBAuth dbAuth;


    private Button registerButton;
    protected EditText email;
    private EditText username;
    protected EditText password;

    private static final String TAG = "EmailPassword";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        dbAuth = new DBAuth(mAuth, getApplicationContext());
        dbAuth.updateUsernameList(); // fetch the usernamelist now so it is ready by the time the user clicks register

        email = findViewById(R.id.email);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);

        // REGISTER button
        registerButton = findViewById(R.id.register);
        registerButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String emailS = email.getText().toString();
                String passwordS = password.getText().toString();
                String usernameS = username.getText().toString();

                if (dbAuth.verifyEmail(emailS)){
                    if (dbAuth.verifyPass(passwordS)){
                        //new UsernameFragment().show(getSupportFragmentManager(), "registering");
                        if (dbAuth.verifyUsername(usernameS)){
                            // all fields are good
                            FirebaseUser user = dbAuth.register(emailS, passwordS, usernameS);
                            if (user == null){
                                email.setError("Email in use"); // Firebase call fails when email is in use
                            }
                            Intent intent = new Intent();
                            setResult(Activity.RESULT_OK, intent);
                            finish();
                        } else {
                            username.setError("Username in use");
                        }
                    } else {
                        password.setError("Password must be >= 6 chars");
                    }
                } else {
                    email.setError("Incorrect email format");
                }
            }
        });


    }


}