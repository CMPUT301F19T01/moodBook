package com.example.moodbook.ui.login;

import android.content.Context;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;

import static androidx.constraintlayout.widget.Constraints.TAG;

/**
 * This class handles interaction with the DB to login and register
 * Citation
 * https://stackoverflow.com/questions/50899160/oncompletelistener-get-results-in-another-class  - Levi Moreira    used to find out what argument to use in .addOnCompleteListener
 * https://firebase.google.com/docs/auth/android/manage-users#update_a_users_profile Used to update username
 */

public class DBAuth {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private CollectionReference collectionReference;
    private ArrayList<String> usernameList;

    public DBAuth(FirebaseAuth mAuth){
        this.mAuth = mAuth;
        this.db = FirebaseFirestore.getInstance();
        this.collectionReference = db.collection("USERS");
        this.usernameList = this.updateUsernameList();
    }

    /**
     * This method verifys that the email and password are filled out. Email is of type email, password is > 6 chars
     * @param email
     * @return
     *      True if email is an email address
     *      False if email is not an email address
     */
    public Boolean verifyEmail(String email){
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
    public Boolean verifyPass(String password){
        return password.length() >= 6;
    }

    /**
     * This method attempts to log a user in
     */
    // https://stackoverflow.com/questions/50899160/oncompletelistener-get-results-in-another-class  - Levi Moreira    used to find out what argument to use in .addOnCompleteListener
    public FirebaseUser login(String email, String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                        } else {
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                        }
                    }
                });
        FirebaseUser user = mAuth.getCurrentUser();
        return user;
    }

    /**
     * This method creates a new user in Firebase
     */
    public FirebaseUser register(String email, String password, String userParam){
        final String username = userParam;
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            createUser(user, username);
                        } else {
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        }
                    }
                });
        FirebaseUser user = mAuth.getCurrentUser();
        return user;
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
        nullData.put("null", null);

        collectionReference.document(uid).collection("MOODS").document("null").set(nullData);
        collectionReference.document(uid).collection("FRIENDS").document("null").set(nullData);
        collectionReference.document(uid).collection("REQUESTS").document("null").set(nullData);

        nullData.put("uid", user.getUid());
        db.collection("usernamelist").document(username).set(nullData); // add username to usernamelist

    }

    /**
     * This method gets all the currently used usernames
     * @return
     *      an ArrayList of usernames
     * https://firebase.google.com/docs/auth/android/manage-users#update_a_users_profile Used to update username
     */
    public ArrayList<String> updateUsernameList(){
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

    /**
     * This method verifys a given username for uniqueness and length
     * @param username
     * @return
     *      true: username is unique and > length 0
     *      false: username is not unique and/or is not > length 0
     */
    public Boolean verifyUsername(String username){
        return (!usernameList.contains(username) && username.length() > 0);

    }

    /**
     * Stores the username in the user's FireBase auth profile
     * @param user
     * @param username
     */
    public void updateUsername(FirebaseUser user, String username){
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(username).build();
        user.updateProfile(profileUpdates) .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d("PROFILE", "User profile updated.");
                }
            }
        });
    }
}
