package com.example.moodbook;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.moodbook.ui.friendMood.FriendMood;
import com.example.moodbook.ui.friendMood.FriendMoodListAdapter;
import com.example.moodbook.ui.myFriends.FriendListAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

public class DBFriend {
    private FirebaseFirestore db;
    private CollectionReference userReference;
    private Context context;
    private String uid;
    private String TAG;         // optional: for log message

    /**
     *
     * @param mAuth
     * @param context
     * @param friendListener
     * @param TAG
     */
    public DBFriend(FirebaseAuth mAuth, Context context, @NonNull EventListener friendListener, String TAG) {
        this.db = FirebaseFirestore.getInstance();
        this.uid = mAuth.getCurrentUser().getUid();
        this.userReference = db.collection("USERS");
        this.context = context;
        this.TAG = TAG;
        this.userReference.document(uid).collection("FRIENDS")
                .addSnapshotListener(friendListener);
    }

    /**
     * Default TAG version
     * @param mAuth
     * @param context
     */
    public DBFriend(FirebaseAuth mAuth, Context context, @NonNull EventListener friendListener) {
        this(mAuth, context, friendListener, DBFriend.class.getSimpleName());
    }


    /**
     * This is used by FriendList to get all the user's friends (username, uid) in the database
     * from the user's friend collection
     */
    public static EventListener<QuerySnapshot> getFriendListener (
            final FriendListAdapter friendAdapter){
        return new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @NonNull FirebaseFirestoreException e) {
                if (friendAdapter != null) {
                    // clear the old list
                    friendAdapter.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        // ignore null item
                        if (doc.getId().equals("null")) continue;
                        // Adding friend from FireStore
                        if (doc.getData() != null && doc.getData().get("uid") != null) {
                            MoodbookUser friendUser = new MoodbookUser(doc.getId(), (String) doc.getData().get("uid"));
                            friendAdapter.add(friendUser);
                        }
                    }
                }
            }
        };
    }


    /**
     * This is used by FriendMoods to get the most recent mood from all the user's friends in the database
     * starting from the user's friend collection
     */
    public static EventListener<QuerySnapshot> getFriendRecentMoodListener (
            final FirebaseFirestore db, final FriendMoodListAdapter friendMoodListAdapter){
        return new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @NonNull FirebaseFirestoreException e) {
                if (friendMoodListAdapter != null) {
                    // clear the old list
                    friendMoodListAdapter.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        // ignore null item
                        if (doc.getId().equals("null")) continue;
                        // Get most recent mood for the friend from FireStore
                        if (doc.getData() != null && doc.getData().get("uid") != null) {
                            // get uid and username of the friend
                            String username = doc.getId();
                            String uid = (String) doc.getData().get("uid");
                            MoodbookUser friendUser = new MoodbookUser(username, uid);
                            // start getting most recent moodID
                            getRecentMoodID(db, friendUser, friendMoodListAdapter);
                        }
                    }
                }
            }
        };
    }


    /**
     * This set recentMoodID in the database for a user
     */
    public static void setRecentMoodID(FirebaseFirestore db, String currentUid, String recentMoodID) {
        DocumentReference currentUserDoc = db.collection("USERS").document(currentUid);
        currentUserDoc.update("recent_moodID", recentMoodID);
    }

    /**
     * This get recentMoodID in the database for a user
     */
    public static void getRecentMoodID(final FirebaseFirestore db, final MoodbookUser user,
                                       final FriendMoodListAdapter friendMoodListAdapter) {
        final DocumentReference currentUserDoc = db.collection("USERS")
                .document(user.getUid());
        currentUserDoc
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot != null) {
                            // get mood docId from moodCount
                            String recentMoodID = documentSnapshot.getString("recent_moodID");
                            if (recentMoodID != null) {
                                getMoodFromMoodID(db, user, recentMoodID, friendMoodListAdapter);
                            }
                        } else {
                            //Log.d(TAG, "no data from "+currentUid);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Log.d(TAG, e.toString());
                    }
                });
    }

    public static void getMoodFromMoodID(final FirebaseFirestore db, final MoodbookUser user, final String moodID,
                                         final FriendMoodListAdapter friendMoodListAdapter) {
        DocumentReference currentMoodDoc =  db.collection("USERS")
                .document(user.getUid()).collection("MOODS").document(moodID);
        currentMoodDoc
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot != null && documentSnapshot.getData() != null) {
                            // get mood based on moodID
                            Mood mood = DBMoodSetter.getMoodFromData(documentSnapshot.getData());
                            if (mood != null) {
                                mood.setDocId(moodID);
                                FriendMood friendMood = new FriendMood(user, mood);
                                friendMoodListAdapter.add(friendMood);
                            }
                        } else {
                            //Log.d(TAG, "no data from "+currentUid);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Log.d(TAG, e.toString());
                    }
                });
    }

}
