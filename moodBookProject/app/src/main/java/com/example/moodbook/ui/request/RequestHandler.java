package com.example.moodbook.ui.Request;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.moodbook.MoodbookUser;
import com.example.moodbook.ui.myRequests.RequestsAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

import java.util.HashMap;

/**
 * This class handles interaction with the db to send requests
 */

public class RequestHandler {
    private CollectionReference userReference;
    private String uid;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String TAG;
    private Context context;


    public RequestHandler(FirebaseAuth mAuth, Context context, String TAG){
        this.mAuth = mAuth;
        this.db = FirebaseFirestore.getInstance();
        this.context = context;
        this.uid = mAuth.getCurrentUser().getUid();
        this.userReference = db.collection("USERS");
    }

    public RequestHandler(FirebaseAuth mAuth, Context context){
        this(mAuth, context, RequestHandler.class.getSimpleName());
    }

    public void setRequestListListener(@NonNull RequestsAdapter requestsAdapter) {
        this.userReference.document(uid).collection("REQUESTS")
                .addSnapshotListener(getRequestListener(requestsAdapter));
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
                        Log.d("TESTING", "no such doc");
                    }
                } else {
                    Log.d("TESTING", "get failed with ", task.getException());
                }
            }
        });
    }

    /**
     * This methods gets the requests from DB and shows it in the listview
     * @param requestsAdapter
     * @return
     */
    private EventListener<QuerySnapshot> getRequestListener(@NonNull final RequestsAdapter requestsAdapter) {
        return new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @NonNull FirebaseFirestoreException e) {
                // clear the old list
                requestsAdapter.clear();
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                    // ignore null item
                    if (!doc.getId().equals("null")) {
                        // Adding requestuser from FireStore
                        if(doc.getData() != null && doc.getData().get("uid") != null) {
                            MoodbookUser user = new MoodbookUser(doc.getId(), (String) doc.getData().get("uid"));
                            requestsAdapter.addItem(user);
                        }
                    }
                }
            }
        };
    }

    public void addFriend(final MoodbookUser acceptFriend, final String myUsername){

        final String username = myUsername;
        final CollectionReference friendsReference = this.userReference.document(acceptFriend.getUid()).collection("FRIENDS");
        HashMap<String, Object> data = new HashMap<>();
        data.put("uid", uid);
        friendsReference.document(username).set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        showStatusMessage("Added successfully: " + username);
                        Log.d(TAG, acceptFriend.getUid());
                        Log.d(TAG, username);
                        Log.d(TAG, "Added Friend.");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showStatusMessage("Adding failed for " + username + ": " + e.toString());
                    }
                });
    }

    public void addToFollowerList(final MoodbookUser acceptFriend, final String myUsername){
        final CollectionReference followersRef = this.userReference.document(uid).collection("FOLLOWERS");
        HashMap<String, Object> data = new HashMap<>();
        data.put("uid", acceptFriend.getUid());
        followersRef.document(acceptFriend.getUsername()).set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        showStatusMessage("Added successfully.");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showStatusMessage("Adding failed." + e.toString());
                    }
                });
    }

    public void removeRequest( final String username){
        final CollectionReference collectionReference = this.userReference.document(this.uid).collection("REQUESTS");
        collectionReference.document(username).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        showStatusMessage("Accepted Friend Request: " + username);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showStatusMessage("Deleting failed for  " + username + ": " + e.toString());
                    }
                });
    }

    /**
     * This is a helper method to show status messages
     * @param message
     *  This is a string that contains the status to be shown
     */
    private void showStatusMessage(String message) {
        Log.w(TAG, message);
        if(context != null) {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
        }
    }
}