package com.example.moodbook;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.moodbook.ui.friendMood.FriendMood;
import com.example.moodbook.ui.friendMood.FriendMoodFragment;
import com.example.moodbook.ui.friendMood.FriendMoodListAdapter;
import com.example.moodbook.ui.myFriendMoodMap.MyFriendMoodMapFragment;
import com.example.moodbook.ui.myFriends.FriendListAdapter;
import com.example.moodbook.ui.myFriends.MyFriendsFragment;
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


/**
 * Used by Friend related pages: MyFriends, FriendMood, FriendMoodMap
 */
public class DBFriend {

    public interface FriendRecentMoodListListener {
        void beforeGettingFriendMoodList();
        void onGettingFriendMood(FriendMood item);
    }

    private FirebaseFirestore db;
    private CollectionReference userReference;
    private Context context;
    private String uid;
    private String TAG;         // optional: for log message
    private FriendRecentMoodListListener listListener;

    /**
     *
     * @param mAuth
     * @param context
     * @param TAG
     */
    public DBFriend(FirebaseAuth mAuth, Context context, String TAG) {
        this.db = FirebaseFirestore.getInstance();
        this.uid = mAuth.getCurrentUser().getUid();
        this.userReference = db.collection("USERS");
        this.context = context;
        this.TAG = TAG;
    }

    /**
     * Default TAG version
     * @param mAuth
     * @param context
     */
    public DBFriend(FirebaseAuth mAuth, Context context) {
        this(mAuth, context, DBFriend.class.getSimpleName());
    }

    /**
     * This is used by MyFriends
     * @param friendListAdapter
     */
    public void setFriendListListener(FriendListAdapter friendListAdapter) {
        this.userReference.document(uid).collection("FRIENDS")
                .addSnapshotListener(getFriendEventListener(friendListAdapter));
    }

    /**
     * This is used by FriendMood and FriendMoodMap
     * @param listListener
     */
    public void setFriendRecentMoodListener(FriendRecentMoodListListener listListener) {
        this.listListener = listListener;
        this.userReference.document(uid).collection("FRIENDS")
                .addSnapshotListener(
                        getFriendRecentMoodEventListener()
                );
    }

    /**
     * This set recentMoodID in the database for a user
     */
    public static void setRecentMoodID(FirebaseFirestore db, String currentUid, String recentMoodID) {
        DocumentReference currentUserDoc = db.collection("USERS").document(currentUid);
        currentUserDoc.update("recent_moodID", recentMoodID);
    }


    /**
     * This EventListener is for MyFriends to get all the user's friends (username, uid) in the database
     * from the user's friend collection
     */
    private EventListener<QuerySnapshot> getFriendEventListener (final FriendListAdapter friendAdapter){
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
     * This EventListener is for FriendMood and FriendMoodMap to get the most recent mood
     * from all the user's friends in the database starting from the user's friend collection
     */
    private EventListener<QuerySnapshot> getFriendRecentMoodEventListener (){
        return new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @NonNull FirebaseFirestoreException e) {
                /*// clear the old list
                friendMoodListAdapter.clear();*/
                listListener.beforeGettingFriendMoodList();
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
                        getRecentMoodID(friendUser);
                    }
                }
            }
        };
    }

    /**
     * This get recentMoodID in the database for a user
     * @param user
     */
    private void getRecentMoodID(final MoodbookUser user) {
        final DocumentReference currentUserDoc = this.userReference.document(user.getUid());
        currentUserDoc
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot != null) {
                            // get mood docId from moodCount
                            String recentMoodID = documentSnapshot.getString("recent_moodID");
                            if (recentMoodID != null) {
                                getMoodFromMoodID(user, recentMoodID);
                            }
                            else {
                                Log.d(TAG, "no recent moodID from "+user.getUsername());
                            }
                        } else {
                            Log.d(TAG, "no recent moodID from "+user.getUsername());
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


    /**
     * This get mood based on moodID
     * @param user
     * @param moodID
     */
    private void getMoodFromMoodID(final MoodbookUser user, final String moodID) {
        DocumentReference currentMoodDoc =  this.userReference.document(user.getUid())
                .collection("MOODS").document(moodID);
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
                                listListener.onGettingFriendMood(friendMood);
                            }
                        } else {
                            Log.d(TAG, "no data from mood "+moodID);
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


    /**
     * This is used by FriendMood and FriendMoodMap
     * @param friendMoodListAdapter
     */
    /*public void setFriendRecentMoodListener(FriendMoodListAdapter friendMoodListAdapter) {
        this.userReference.document(uid).collection("FRIENDS")
                .addSnapshotListener(getFriendRecentMoodEventListener(friendMoodListAdapter));
    }*/


    /**
     * This EventListener is for FriendMoods to get the most recent mood from all the user's friends in the database
     * starting from the user's friend collection
     */
    /*private EventListener<QuerySnapshot> getFriendRecentMoodEventListener (
            @NonNull final FriendMoodListAdapter friendMoodListAdapter){
        return new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @NonNull FirebaseFirestoreException e) {
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
                        getRecentMoodID(friendUser, friendMoodListAdapter);
                    }
                }
            }
        };
    }*/

    /**
     * This get recentMoodID in the database for a user
     * @param user
     * @param friendMoodListAdapter
     */
    /*private void getRecentMoodID(final MoodbookUser user,
                                 final FriendMoodListAdapter friendMoodListAdapter) {
        final DocumentReference currentUserDoc = this.userReference.document(user.getUid());
        currentUserDoc
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot != null) {
                            // get mood docId from moodCount
                            String recentMoodID = documentSnapshot.getString("recent_moodID");
                            if (recentMoodID != null) {
                                getMoodFromMoodID(user, recentMoodID, friendMoodListAdapter);
                            }
                            else {
                                Log.d(TAG, "no recent moodID from "+user.getUsername());
                            }
                        } else {
                            Log.d(TAG, "no recent moodID from "+user.getUsername());
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, e.toString());
                    }
                });
    }*/


    /**
     * This get mood based on moodID
     * @param user
     * @param moodID
     * @param friendMoodListAdapter
     */
    /*public void getMoodFromMoodID(final MoodbookUser user, final String moodID,
                                  final FriendMoodListAdapter friendMoodListAdapter) {
        DocumentReference currentMoodDoc =  this.userReference.document(user.getUid())
                .collection("MOODS").document(moodID);
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
                            Log.d(TAG, "no data from mood "+moodID);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, e.toString());
                    }
                });
    }*/

}
