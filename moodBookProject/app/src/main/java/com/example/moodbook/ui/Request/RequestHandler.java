package com.example.moodbook.ui.Request;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.moodbook.DBCollectionListener;
import com.example.moodbook.MoodbookUser;
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
 * This class handles interaction with the db to send and accept requests.
 */

public class RequestHandler {
    private CollectionReference userReference;
    private String uid;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String TAG;
    private Context context;
    private DBCollectionListener listListener;


    public RequestHandler(FirebaseAuth mAuth, Context context, String TAG){
        this.mAuth = mAuth;
        this.db = FirebaseFirestore.getInstance();
        this.context = context;
        this.uid = mAuth.getCurrentUser().getUid();
        this.userReference = db.collection("USERS");
        this.TAG = TAG;
    }

    public RequestHandler(FirebaseAuth mAuth, Context context){
        this(mAuth, context, RequestHandler.class.getSimpleName());
    }

    /**
     * This method listens to any changes in the user's REQUESTS db document
     * @param listListener
     */
    public void setRequestListListener(@NonNull DBCollectionListener listListener) {
        this.listListener = listListener;
        this.userReference.document(uid).collection("REQUESTS")
                .addSnapshotListener(getRequestListener());
    }

    /**
     * This method adds a request to the given user's db document
     * @param addUser
     * This is the username to add
     * @param uidp
     * This is the current user's uid
     * @param usernamep
     * This is the current user's username
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
                        collectionReference
                                .document(document.getString("uid"))
                                .collection("REQUESTS")
                                .document(username)
                                .set(data); // adding the request to the addUser's REQUEST collection
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
     * This methods gets the requests from DB and shows it in a ListView.
     * @return
     */
    private EventListener<QuerySnapshot> getRequestListener() {
        return new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @NonNull FirebaseFirestoreException e) {
                listListener.beforeGettingList();
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                    // ignore null item
                    if (!doc.getId().equals("null")) {
                        if(doc.getData() != null && doc.getData().get("uid") != null) {
                            MoodbookUser user = new MoodbookUser(doc.getId(),
                                    (String) doc.getData().get("uid"));
                            listListener.onGettingItem(user);
                        }
                    }

                }
                listListener.afterGettingList();
            }
        };
    }

    /**
     * This method is used when a user accepts a request.
     * This adds a friend to the accepted user's "FRIENDS" collection on the database.
     * @param acceptFriend
     * This is a MoodbookUser object whose request got accepted by the user.
     * @param myUsername
     * This is the username of the user that accepted the request.
     */
    public void addFriend(final MoodbookUser acceptFriend, final String myUsername){

        final String username = myUsername;
        final CollectionReference friendsReference = this.userReference
                .document(acceptFriend.getUid())
                .collection("FRIENDS");
        HashMap<String, Object> data = new HashMap<>();
        data.put("uid", uid);
        friendsReference.document(username).set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        showStatusMessage("Added successfully: " + username);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showStatusMessage("Adding failed for " + username + ": " + e.toString());
                    }
                });
    }

    /**
     * This method allows a user to follow back another user after accepting a request
     * @param acceptFriend
     * This is a MoodbookUser object whose request got accepted by the user.
     * @param myUsername
     * This is the username of the user that accepted the request.
     */
    public void followBack(final String myUsername, final MoodbookUser acceptFriend ) {
        final String username = myUsername;
        final CollectionReference friendsReference
                = this.userReference.document(uid).collection("FRIENDS");
        HashMap<String, Object> data = new HashMap<>();
        data.put("uid", uid);
        friendsReference.document(acceptFriend.getUsername()).set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        showStatusMessage("Added successfully.");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showStatusMessage("Adding failed.");
                    }
                });
    }

    /**
     * This method adds a follower to the user's "FOLLOWERS" collection on the database.
     * @param acceptFriend
     * This is a MoodbookUser object whose request got accepted by the user.
     * @param myUsername
     * This is the username of the user that accepted the request.
     */
    public void addToFollowerList(final MoodbookUser acceptFriend, final String myUsername){
        final CollectionReference followersRef
                = this.userReference.document(uid).collection("FOLLOWERS");
        HashMap<String, Object> data = new HashMap<>();
        data.put("uid", acceptFriend.getUid());
        followersRef.document(acceptFriend.getUsername()).set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i("FollowerListInfo", "Added to followers list successfully.");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i("FollowerListInfo", "Failed to add to followers list.");
                    }
                });
    }

    /**
     * This method removes a request.
     * @param username
     */
    public void removeRequest( final String username){
        final CollectionReference collectionReference
                = this.userReference.document(this.uid).collection("REQUESTS");
        collectionReference.document(username).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        showStatusMessage("Declined request");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showStatusMessage("Decline failed for  " + username + ": " + e.toString());
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