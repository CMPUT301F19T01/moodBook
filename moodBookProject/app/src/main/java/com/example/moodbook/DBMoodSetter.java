package com.example.moodbook;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Document;

import java.util.HashMap;

public class DBMoodSetter {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private CollectionReference userReference;
    private Context context;
    private String uid;

    public DBMoodSetter(FirebaseAuth mAuth, Context context){
        this.mAuth = mAuth;
        this.db = FirebaseFirestore.getInstance();
        this.uid = mAuth.getCurrentUser().getUid();
        this.userReference = db.collection("USERS");
        this.context = context;
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
        HashMap<String, Object> data = new HashMap<>();
        data.put("date",mood.getDateText());
        data.put("time",mood.getTimeText());
        data.put("emotion",mood.getEmotionText());
        data.put("situation",mood.getSituation());
        data.put("reason_text",mood.getReasonText());
//        data.put("location_lat", mood.getLocationLatitude());
//        data.put("location_lon", mood.getLocationLongtitude());
        return data;
    }
}
