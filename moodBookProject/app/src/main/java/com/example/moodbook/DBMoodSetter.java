package com.example.moodbook;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseError;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Document;

import java.util.HashMap;

public class DBMoodSetter {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private CollectionReference userReference;
    private CollectionReference intReference;
    private Context context;
    private String uid;
    private FieldValue var;

    public DBMoodSetter(FirebaseAuth mAuth, Context context){
        this.mAuth = mAuth;
        this.db = FirebaseFirestore.getInstance();
        this.uid = mAuth.getCurrentUser().getUid();
        this.userReference = db.collection("USERS");
        this.intReference = db.collection("int");
        this.context = context;
    }

    //writes to database the last int that it used
    public void setInt() {
        DocumentReference intRef = intReference.document("count");
        // Atomically increment the population of the city by 50.
        intRef.update("count", FieldValue.increment(1));
    }


    //gets from database what int it last used, so it could start counting from there
    public void getInt() {

    }

    public void addMood(Mood mood) {
        HashMap<String, Object> data = getMoodData(mood);
        String docId = getMoodDocId(mood);
        userReference.document(uid).collection("MOODS").document(docId).set(data);
    }

    public void removeMood(Mood mood) {
        String docId = getMoodDocId(mood);
        // remove selected city
        userReference.document(uid).collection("MOODS").document(docId).delete();
    }

    private String getMoodDocId(Mood mood) {
        return mood.getDateText()+"_"+mood.getTimeText()+"_"+mood.getEmotionText();
    }

    private HashMap<String, Object> getMoodData(Mood mood) {
        Location location = mood.getLocation();
        HashMap<String, Object> data = new HashMap<>();
        data.put("date",mood.getDateText());
        data.put("time",mood.getTimeText());
        data.put("emotion",mood.getEmotionText());
        data.put("situation",mood.getSituation());
        data.put("reason_text",mood.getReasonText());
        data.put("location_lat", location==null ? null : location.getLatitude());
        data.put("location_lon", location==null ? null : location.getLongitude());
        return data;
    }

    private HashMap<String, Object> getMoodInt(int i) {
        HashMap<String, Object> data = new HashMap<>();
        data.put("counter", i);
        return data;
    }
}