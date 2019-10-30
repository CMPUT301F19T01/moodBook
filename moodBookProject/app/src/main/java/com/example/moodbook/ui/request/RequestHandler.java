package com.example.moodbook.ui.request;

import android.util.Log;

import com.example.moodbook.ui.login.DBAuth;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

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
}
