package com.example.moodbook;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.model.value.ReferenceValue;

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

    public void addMood(final String moodstate){
        final HashMap<String, Object> data = new HashMap<>();
        final HashMap<String, Object> mood = new HashMap<>();

        userReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                for (QueryDocumentSnapshot doc: queryDocumentSnapshots){
                    if (doc.contains("moodCount")){
                        Log.d("Mood Count", String.valueOf(doc.getData().get("moodCount")));
                        int moodCount = doc.get("moodCount", Integer.class) ;//Long and .getlong

                        if(doc.getData().get("moodCount")==null) {
                            Log.d("Is Null:", "Yes!!!! It is null");
                            System.out.println(doc.getData());

                        }else{
                            System.out.println(doc.getData());
                        }

                        Log.d("Mood Count", String.valueOf(moodCount));
                        int newCount = moodCount + 1;
//                    data.put("moodID",String.valueOf(doc.getData().get("moodCount")));
                        data.put("moodID",String.valueOf(doc.getData().get("moodCount")));
                        data.put("moodstate", moodstate);
                        data.put("moodreason","Rude");
//                    final String userName = String.valueOf(doc.getData().get("username"));

//                    final int count = Integer.valueOget("username")f(String.valueOf(doc.getData().get("moodCount")))+1;
                        mood.put("moodCount",newCount);
//                    mood.put("username",userName);
                        userReference.document(uid).update(mood);
//                    Log.d("id",userReference.document(uid).getId());
//                    userReference.document(uid).set(mood);
//                    doc.getData().put("moodCount","2");
                        userReference.document(uid).collection("MOODS").document(String.valueOf(doc.getData().get("moodCount"))).set(data);

                    }else{
                        Log.d("Mood Not Found","Cannot find Anything");
                    }

//
                }
            }
        });



//        userReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
//            @Override
//            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
//                for (QueryDocumentSnapshot doc: queryDocumentSnapshots){
////                    Log.d("Mood Count", String.valueOf(doc.getData().get("moodCount")));
//                     int moodCount = doc.get("moodCount", Integer.class) ;//Long and .getlong
//
//                    if(doc.getData().get("moodCount")==null) {
//                        Log.d("Is Null:", "Yes!!!! It is null");
//                        System.out.println(doc.getData());
//
//                    }else{
//                        System.out.println(doc.getData());
//                    }
//
//                     Log.d("Mood Count", String.valueOf(moodCount));
//                    int newCount = moodCount + 1;
////                    data.put("moodID",String.valueOf(doc.getData().get("moodCount")));
//                    data.put("moodID",String.valueOf(doc.getData().get("moodCount")));
//                    data.put("moodstate", moodstate);
//                    data.put("moodreason","Rude");
////                    final String userName = String.valueOf(doc.getData().get("username"));
//
////                    final int count = Integer.valueOget("username")f(String.valueOf(doc.getData().get("moodCount")))+1;
//                    mood.put("moodCount",newCount);
////                    mood.put("username",userName);
//                    userReference.document(uid).update(mood);
////                    Log.d("id",userReference.document(uid).getId());
////                    userReference.document(uid).set(mood);
////                    doc.getData().put("moodCount","2");
//                    userReference.document(uid).collection("MOODS").document("6").set(data);
//
//                }
//            }
//        });
//        if (moodCount[0] != null){
//            Log.d("Mood Count", moodCount[0]);
//            data.put("moodID", moodCount[0]);
//        }





//        userReference.document(uid).collection("MOODS").document("6").set(data);

    }



}
