package com.example.moodbook.ui.Request;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.moodbook.DBMoodSetter;
import com.example.moodbook.Mood;
import com.example.moodbook.MoodInvalidInputException;
import com.example.moodbook.MoodListAdapter;
import com.example.moodbook.R;
import com.example.moodbook.ui.myRequests.RequestUser;
import com.example.moodbook.ui.myRequests.RequestsAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class handles interaction with the db to send requests
 */

public class RequestHandler {
    private static CollectionReference userReference;
    private static String uid;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String TAG;
    private Context context;

    public RequestHandler(FirebaseAuth mAuth, Context context){
        this.mAuth = mAuth;
        this.db = FirebaseFirestore.getInstance();
        this.context = context;
        this.uid = mAuth.getCurrentUser().getUid();
        this.userReference = db.collection("USERS");
        this.context = context;
    }


    public RequestHandler(FirebaseAuth mAuth, FirebaseFirestore db){
        this.mAuth = mAuth;
        this.db = db;
        this.context = context;
    }

    public RequestHandler(FirebaseAuth mAuth, Context context, @NonNull EventListener requestHistoryListener){
        this(mAuth, context);
        userReference.document(uid).collection("REQUESTS")
                .addSnapshotListener(requestHistoryListener);
    }

    public RequestHandler(FirebaseAuth mAuth, Context context, @NonNull EventListener requestHistoryListener, String TAG){
        this(mAuth, context, requestHistoryListener);
        this.TAG = TAG;
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
        final CollectionReference collectionReference = db.collection("USERS");
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


    public static void getRequests(final RequestsAdapter requestsAdapter, String uid, CollectionReference userReference){
        userReference.document(uid).collection("REQUESTS").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                List<RequestUser> ids = new ArrayList<>();
                if (task!=null){
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String id = document.getId();
                            RequestUser user = getRequestFromData(id);
                            requestsAdapter.addItem(user);
                        }
                    }
                }
            }
        });
    }
//
//    /**
//     * This methods gets the requests from DB and shows it in the listview
//     * @param requestAdapter
//     * @return
//     */
//    public static EventListener<QuerySnapshot> getRequestListener(final RequestsAdapter requestAdapter) {
//        return new EventListener<QuerySnapshot>() {
//            @Override
//            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @NonNull FirebaseFirestoreException e) {
//                if(requestAdapter != null) {
//                    // clear the old list
//                    requestAdapter.clear();
//                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
//                        // ignore null item
//                        if (doc.getId() != "null") {
//                            // Adding requestee from FireStore
//                            RequestUser user = RequestHandler.getRequestFromData(doc.getData());
//                            if (user != null) {
//                                user.setUsername(doc.getId());
//                                requestAdapter.addItem(user);
//                            }
//                        }
//                    }
//                }
//            }
//        };
//    }

    public static RequestUser getRequestFromData(String data) {
        RequestUser newRequestee = null;
        newRequestee = new RequestUser(data);
        return newRequestee;
    }
//
//    private void showStatusMessage(String message) {
//        Log.w(TAG, message);
//        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
//    }
}