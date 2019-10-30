package com.example.moodbook.ui.request;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.moodbook.ui.login.DBAuth;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;

public class RequestHandler {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private CollectionReference collectionReference;
    private ArrayList<String> usernameList;
    private DBAuth dbAuth;

    public RequestHandler(FirebaseAuth mAuth){
        this.mAuth = mAuth;
        this.db = FirebaseFirestore.getInstance();
        this.collectionReference = db.collection("USERS");
        this.dbAuth = new DBAuth(mAuth);
        this.usernameList = dbAuth.updateUsernameList();
    }

    /**
     * This method verifys that the username to be added exists
     * @param username
     * @return
     *      true: username exists
     *      false: username does not exist
     */
    public Boolean verifyRequest(String username){
        dbAuth.updateUsernameList();
        Log.d("REQUEST", usernameList.get(0));
        return usernameList.contains(username);
    }

    /**
     * This method adds a request to the given user's db document
     * @param addUser -- the username to add
     * @param uidp  -- the current user's uid
     * @param usernamep  -- the current user's username
     */
    public void sendRequest(String addUser, String uidp, String usernamep){
        final String uid = uidp;
        final String username = usernamep;
        // getting the addUser's uid
        DocumentReference docRef = db.collection("usernamelist").document(addUser);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()){
                        HashMap<String, Object> data = new HashMap<>();
                        data.put("uid", uid);
                        collectionReference.document(document.getString("uid")).collection("REQUESTS").document(username).set(data); // adding the request to the addUser's REQUEST collection
                    } else {
                        Log.d("TESTINGG", "no such doc");
                    }
                } else {
                    Log.d("TESTING", "get failed with ", task.getException());
                }
            }
        });
    }
}