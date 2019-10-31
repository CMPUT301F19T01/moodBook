package com.example.moodbook;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Document;

import java.util.HashMap;
import java.util.Map;

public class DBMoodSetter {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private CollectionReference userReference;
    private CollectionReference intReference;
    private DocumentReference intRef;
    private Context context;
    private String uid;
    private String TAG;
    private String moodID;

    public DBMoodSetter(FirebaseAuth mAuth, Context context){
        this.mAuth = mAuth;
        this.db = FirebaseFirestore.getInstance();
        this.uid = mAuth.getCurrentUser().getUid();
        this.userReference = db.collection("USERS");
        this.context = context;
        this.intReference = db.collection("int");
    }

    public DBMoodSetter(FirebaseAuth mAuth, Context context, String TAG){
        this(mAuth, context);
        this.TAG = TAG;
    }

    public DBMoodSetter(FirebaseAuth mAuth, Context context, @NonNull EventListener moodHistoryListener){
        this(mAuth, context);
        userReference.document(uid).collection("MOODS")
                .addSnapshotListener(moodHistoryListener);
    }

    public DBMoodSetter(FirebaseAuth mAuth, Context context, @NonNull EventListener moodHistoryListener, String TAG){
        this(mAuth, context, moodHistoryListener);
        this.TAG = TAG;
    }

    public void setInt() {
        intRef = intReference.document("count");
        // increment moodCount by 1
        intRef.update("mood_Count", FieldValue.increment(1));

    }
    //gets from database what int it last used, so it could start counting from there
    public void updateDocID(final Mood mood) {
        DocumentReference intRef = db.collection("int").document("count");
        intRef
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot!=null){
                            Double m = documentSnapshot.getDouble("mood_Count");
                            moodID = String.valueOf(m);
                            addMood(mood);
                            setInt();
                            //moodID = Integer.valueOf(md.intValue());
                        }
                        else{

                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, e.toString());
                    }
                });

    }


    public void addMood(final Mood mood) {
        Map<String, Object> data = getDataFromMood(mood);
        // TODO: use the mood docId generated from db counter
       // final String docId = mood.toString();
        final String docId = moodID;
        CollectionReference moodReference = userReference.document(uid).collection("MOODS");
        if(TAG != null) {
            moodReference.document(docId).set(data)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            showStatusMessage("Added successfully: " + mood.toString());
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            showStatusMessage("Adding failed for "+mood.toString()+": " + e.toString());
                        }
                    });
        }
        else {
            moodReference.document(docId).set(data);
        }
    }

    public void removeMood(final Mood mood) {
        final String docId = mood.getDocId();
        CollectionReference moodReference = userReference.document(uid).collection("MOODS");
        // remove selected city
        moodReference.document(docId).delete();
        if(TAG != null) {
            moodReference.document(docId).delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            showStatusMessage("Deleted successfully: " + mood.toString());
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            showStatusMessage("Deleting failed for "+mood.toString()+": " + e.toString());
                        }
                    });
        }
        else {
            moodReference.document(docId).delete();
        }
    }

    private Map<String, Object> getDataFromMood(Mood mood) {
        Location location = mood.getLocation();
        Map<String, Object> data = new HashMap<>();
        data.put("date",mood.getDateText());
        data.put("time",mood.getTimeText());
        data.put("emotion",mood.getEmotionText());
        data.put("reason_text",mood.getReasonText());
        data.put("situation",mood.getSituation());
        data.put("location_lat", location==null ? null : location.getLatitude());
        data.put("location_lon", location==null ? null : location.getLongitude());
        return data;
    }

    public Mood getMoodFromData(Map<String, Object> data) {
        Location location = null;
        Object location_lat = data.get("location_lat");
        Object location_lon = data.get("location_lon");
        if(location_lat != null && location_lon != null) {
            location = new Location(LocationManager.GPS_PROVIDER);
            location.setLatitude((double)location_lat);
            location.setLongitude((double)location_lat);
        }
        Mood newMood = null;
        try {
            newMood = new Mood((String)data.get("date")+" "+(String)data.get("time"),
                    (String)data.get("emotion"), (String)data.get("reason_text"), null,
                    (String)data.get("situation"), location);
        } catch (MoodInvalidInputException e) {
            e.printStackTrace();
        }
        return newMood;
    }

    private void showStatusMessage(String message) {
        Log.w(TAG, message);
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }
}
