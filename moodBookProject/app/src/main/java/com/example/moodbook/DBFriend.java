package com.example.moodbook;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.moodbook.ui.myFriends.FriendListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

public class DBFriend {
    private FirebaseFirestore db;
    private static FirebaseStorage storage;
    private CollectionReference userReference;
    private Context context;
    private String uid;
    private String TAG;         // optional: for log message

    public DBFriend(FirebaseAuth mAuth, Context context) {
        this.db = FirebaseFirestore.getInstance();
        this.storage = FirebaseStorage.getInstance();
        this.uid = mAuth.getCurrentUser().getUid();
        this.userReference = db.collection("USERS");
        this.context = context;
        this.TAG = DBFriend.class.getSimpleName();
    }

    public DBFriend(FirebaseAuth mAuth, Context context, String TAG) {
        this(mAuth, context);
        this.TAG = TAG;
    }

    public DBFriend(FirebaseAuth mAuth, Context context, @NonNull EventListener friendListener) {
        this(mAuth, context);
        userReference.document(uid).collection("FRIENDS")
                .addSnapshotListener(friendListener);
    }

    public DBFriend(FirebaseAuth mAuth, Context context, @NonNull EventListener friendListener, String TAG) {
        this(mAuth, context, friendListener);
        this.TAG = TAG;
    }

    /**
     * This set recentMoodID in the database for a user
     */
    public void setRecentMoodID(String currentUid, String recentMoodID) {
        DocumentReference currentUserDoc = userReference.document(currentUid);
        currentUserDoc.update("recent_moodID", recentMoodID);
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

}
