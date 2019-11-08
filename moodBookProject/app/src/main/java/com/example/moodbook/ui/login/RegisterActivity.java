package com.example.moodbook.ui.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.moodbook.R;
import com.example.moodbook.data.UsernameList;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * This activity handles registration
 * Citation: https://firebase.google.com/docs/auth/android/manage-users?authuser=0
 */

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    protected DBAuth dbAuth;
    private UsernameList usernameList;


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
        dbAuth = new DBAuth(mAuth, FirebaseFirestore.getInstance());

        usernameList = new UsernameList(FirebaseFirestore.getInstance());
        usernameList.updateUsernameList(); // fetch the usernamelist now so it is ready by the time the user clicks register

        email = findViewById(R.id.email);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);

        // Register is not not modularized because FireBase calls are asynchronous. Since they are asynchronous, we can't depend on results returned from methods until the onCompleteListener knows that the task is finished
        // REGISTER button
        registerButton = findViewById(R.id.register);
        registerButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                final String emailS = email.getText().toString();
                final String passwordS = password.getText().toString();
                final String usernameS = username.getText().toString();

                if (dbAuth.verifyEmail(emailS)){
                    if (dbAuth.verifyPass(passwordS)){
                        if (usernameList.verifyUsername(usernameS)){
                            // all fields are good

                            mAuth.createUserWithEmailAndPassword(emailS, passwordS)
                                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {
                                                Log.d(TAG, "createUserWithEmail:success");
                                                FirebaseUser user = mAuth.getCurrentUser();
                                                dbAuth.createUser(user, emailS, usernameS);
                                                dbAuth.updateUsername(user, usernameS);
                                                Intent intent = new Intent();
                                                setResult(Activity.RESULT_OK, intent);
                                                finish();

                                            } else {
                                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                                email.setError("Email in use"); // Firebase call fails when email is in use
                                            }
                                        }
                                    });
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
