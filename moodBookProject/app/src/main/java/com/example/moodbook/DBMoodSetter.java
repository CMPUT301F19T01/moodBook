package com.example.moodbook;

import android.content.Context;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class DBMoodSetter {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private CollectionReference moodReference;
    private Context context;

    public DBMoodSetter(FirebaseAuth mAuth, Context context){
        this.mAuth = mAuth;
        this.db = FirebaseFirestore.getInstance();
        this.moodReference = db.collection("MOODS");
        this.context = context;
    }

    public FirebaseUser loggedUser(){
        return mAuth.getCurrentUser();
    }

    public void addMood(String moodstate){
        FirebaseUser user = loggedUser();
        HashMap<String, Object> data = new HashMap<>();
        data.put("moodstate", moodstate);
        String uid = user.getUid();
        moodReference.document(uid).set(data);
    }


}
