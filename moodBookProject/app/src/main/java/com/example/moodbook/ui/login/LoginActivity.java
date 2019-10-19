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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * This activity handles login and registration
 * Citation: https://firebase.google.com/docs/auth/android/manage-users?authuser=0
 * https://stackoverflow.com/questions/43599638/firebase-signinwithemailandpassword-and-createuserwithemailandpassword-not-worki -Sagar Raut   used for mAuthListener
 * https://stackoverflow.com/questions/16812039/how-to-check-valid-email-format-entered-in-edittext  - iversoncru   used for verifying email format
 */
//TODO:
//  change buttons .isEnabled when appropriate

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseFirestore db;
    private  CollectionReference collectionReference;
    protected ArrayList<String> usernameList;
    private Button loginButton;
    private Button registerButton;
    protected EditText email;
    protected EditText password;
    private static final String TAG = "EmailPassword";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Stuck logging in? use the following line once to log out the cached session:
        mAuth.getInstance().signOut();
        db = FirebaseFirestore.getInstance();
        collectionReference = db.collection("USERS");

        usernameList = getUsernameList();

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
                if (verifyEmail(email.getText().toString())){
                    if (verifyPass(password.getText().toString())){
                        login(email.getText().toString(), password.getText().toString());
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
                if (verifyEmail(email.getText().toString())){
                    if (verifyPass(password.getText().toString())){
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

    /**
     * This method verifys that the email and password are filled out. Email is of type email, password is > 6 chars
     * @param email
     * @return
     *      True if email is an email address
     *      False if email is not an email address
     */
    private Boolean verifyEmail(String email){
        //TODO: verify email unique
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    /**
     * This method verifys that the password is filled out.
     * @param password
     * @return
     *      True if password >= 6 chars
     *      False if password is not >= 6 chars
     */
    private Boolean verifyPass(String password){
        return password.length() >= 6;
    }

    /**
     * This method decides how to update the ui based on the user's login status
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

    /**
     * This method attempts to log a user in
     */
    private void login(String email, String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }

    /**
     * This method creates a new user in Firebase
     */
    public void register(String email, String password, String userParam){
        final String username = userParam;
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            createUser(user, username);
                            updateUI(user);
                        } else {
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }

    /**
     * This method creates containers for a new user in the database
     */
    private void createUser(FirebaseUser user, String username){

        String uid = user.getUid();
        Log.d(TAG, "creating user in db:"+ uid);

        // Initialize moodcount
        HashMap<String, Object> data = new HashMap<>();
        data.put("username", username);
        data.put("moodCount", 0);
        collectionReference
                .document(uid)
                .set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "uid stored in db");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "failed:" + e.toString());
                    }
                });

        // Initialize containers

        HashMap<String, Object> nullData = new HashMap<>();
        data.put("null", null);

        db.collection("usernamelist").document(username).set(nullData); // add username to usernamelist
        collectionReference.document(uid).collection("MOODS").document("null").set(nullData);
        collectionReference.document(uid).collection("FRIENDS").document("null").set(nullData);
        collectionReference.document(uid).collection("REQUESTS").document("null").set(nullData);

    }

    /**
     * This method gets all the currently used usernames
     * @return
     *      an ArrayList of usernames
     */
    private ArrayList<String> getUsernameList(){
        FirebaseFirestore db = db = FirebaseFirestore.getInstance();
        final ArrayList<String> usernameList = new ArrayList<>();
        db.collection("usernamelist")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                usernameList.add(document.getId());
                            }
                        } else {
                            Log.w("Email", "Error getting documents: ", task.getException());
                        }
                    }
                });

        return usernameList;
    }
}
