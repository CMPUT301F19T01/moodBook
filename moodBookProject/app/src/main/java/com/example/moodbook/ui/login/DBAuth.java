package com.example.moodbook.ui.login;

import android.util.Log;

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

import java.util.HashMap;

import static androidx.constraintlayout.widget.Constraints.TAG;

/**
 * This class handles interaction with the DB to login and register
 */
public class DBAuth {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    public DBAuth(FirebaseAuth mAuth, FirebaseFirestore db){
        this.mAuth = mAuth;
        this.db = db;
    }

    /**
     * This method verifys that the email and password are filled out. Email is of type email, password is > 6 chars
     * @param email
     *  email string
     * @return
     *      True if email is an email address
     *      False if email is not an email address
     */
    public Boolean verifyEmail(String email){
        //TODO: verify email unique
        return email.contains("@") && email != null && !email.isEmpty() ;
    }

    /**
     * This method verifys that the password is filled out.
     * @param password
     *  password string
     * @return
     *      True if password >= 6 chars
     *      False if password is not >= 6 chars
     */
    public Boolean verifyPass(String password){ return password.length() >= 6;}

    /**
     * This method creates containers for a new user in the database
     * @param user
     *  user in Firebase
     * @param email
     *  Email String
     * @param username
     *  Username string
     * @param phone
     *  Phone number string
     * @param bio
     *  Biography string
     */
    public void createUser(FirebaseUser user, String email, String username , String phone, String bio){

        CollectionReference collectionReference = db.collection("USERS");

        String uid = user.getUid();
        Log.d(TAG, "creating user in db:"+ uid);

        // Initialize moodcount
        HashMap<String, Object> data = new HashMap<>();
        data.put("email", email);
        data.put("username", username);
        data.put("phone",phone);
        data.put("bio", bio);
        data.put("recent_moodID", null);

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

        /* Initialize containers */

        HashMap<String, Object> nullData = new HashMap<>();
        nullData.put("null", null);

        collectionReference.document(uid).collection("MOODS").document("null").set(nullData);
        collectionReference.document(uid).collection("FRIENDS").document("null").set(nullData); //followings
        collectionReference.document(uid).collection("FOLLOWERS").document("null").set(nullData);
        collectionReference.document(uid).collection("REQUESTS").document("null").set(nullData);
        nullData.put("uid", user.getUid());
        db.collection("usernamelist").document(username).set(nullData); // add username to usernamelist

    }


    /**
     * Stores the username in the user's FireBase auth profile
     * @param user
     *  user in Firebase 
     * @param username
     *  Username string
     */
    public void updateUsername(FirebaseUser user, String username){
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(username).build();
        user.updateProfile(profileUpdates) .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d("PROFILE", "MoodbookUser profile updated.");
                }
            }
        });
    }

}
