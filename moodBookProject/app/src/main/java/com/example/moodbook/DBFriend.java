package com.example.moodbook;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.moodbook.ui.friendMood.FriendMood;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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


/**
 * Used by Friend related pages: MyFriends, FriendMood, FriendMoodMap
 */
public class DBFriend {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private CollectionReference userReference;
    private CollectionReference usernamelistRef;
    private Context context;
    private FirebaseUser user;
    private String uid;
    private static String TAG;         // optional: for log message
    private DBCollectionListener listListener;

    /**
     * This create the a new DBFriend object
     * @param mAuth
     *   FirebaseAuth instance
     * @param context
     *   Current content of the application
     * @param TAG
     *   A string that would be printed out to the Logcat
     */
    public DBFriend(FirebaseAuth mAuth, Context context, String TAG) {
        this.mAuth = mAuth;
        this.db = FirebaseFirestore.getInstance();
        this.uid = mAuth.getCurrentUser().getUid();
        this.user = FirebaseAuth.getInstance().getCurrentUser();
        this.userReference = db.collection("USERS");
        this.usernamelistRef = db.collection("usernamelist");
        this.context = context;
        this.TAG = TAG;
    }

    /**
     * This create a new DBFriend object with the default TAG value
     * @param mAuth
     *  FirebaseAuth instance
     * @param context
     * Current content of the application
     */
    public DBFriend(FirebaseAuth mAuth, Context context) {
        this(mAuth, context, DBFriend.class.getSimpleName());
    }

    /**
     * This is a friend list listener used  by FriendMoodFragment
     * @param listListener
     *  A instance of the DBcollectionListener listener
     *  @see DBCollectionListener
     * @see com.example.moodbook.ui.friendMood.FriendMoodFragment
     */
    public void setFriendListListener(@NonNull DBCollectionListener listListener) {
        this.listListener = listListener;
        this.userReference.document(uid).collection("FRIENDS")
                .addSnapshotListener(getUserEventListener());
    }

    /**
     *  This is a follower list listener used  by FriendMoodFragment
     * @param listListener
     *  A instance of the DBcollectionListener listener
     *  @see DBCollectionListener
     *@see com.example.moodbook.ui.followers.MyFollowersFragment
     */
    public void setFollowersListListener(@NonNull DBCollectionListener listListener) {
        this.listListener = listListener;
        this.userReference.document(uid).collection("FOLLOWERS")
                .addSnapshotListener(getUserEventListener());
    }

    /**
     * This is  friend mood list listener used by FriendMood and FriendMoodMap
     * @param listListener
     *  A instance of the DBcollectionListener listener
     *  @see DBCollectionListener
     * @see FriendMood
     * @see com.example.moodbook.ui.myFriendMoodMap.MyFriendMoodMapFragment
     */
    public void setFriendRecentMoodListener(@NonNull DBCollectionListener listListener) {
        this.listListener = listListener;
        this.userReference.document(uid).collection("FRIENDS")
                .addSnapshotListener(getFriendRecentMoodEventListener());
    }

    /**
     * This updates most recent ID in the database
     * @param db
     *  This is an instance of FirebaseFirestore
     * @param currentUid
     *  This is the current users Id on firebase
     * @param recentMoodID
     *  This is the most recent mood of the current user
     */
    public static void setRecentMoodID(FirebaseFirestore db, String currentUid, String recentMoodID) {
        DocumentReference currentUserDoc = db.collection("USERS").document(currentUid);
        currentUserDoc.update("recent_moodID", recentMoodID);
    }

    /**
     * This method is used by MyFriendsFragment to remove a friend that the user follows
     * @param user
     *  The user who wants to remove a friend
     * @param friend
     *  The friend to be removed
     *
     */
    public void removeFriend(final MoodbookUser user, final MoodbookUser friend) {
        removeFriend(user, friend, true);
    }

    /**
     * This method is used by MyFollowersFragment to remove a follower who was previously accepted by the user
     * @param user
     *  The user who wants to remove a friend
     * @param follower
     *  The follower to be removed
     */
    public void removeFollower(final MoodbookUser user, final MoodbookUser follower) {
        removeFollower(user, follower, true);
    }

    /**
     *This method goes to user's friend list of a user and removes the friend's username if the boolean toRemoveFollower is true
     * @param user
     *  The user who wants to remove a friend
     * @param friend
     *  The friend who might be removed
     * @param toRemoveFriend
     *  The boolean that determines if a friend is removed or not
     */
    private void removeFriend(final MoodbookUser user, final MoodbookUser friend,
                              final boolean toRemoveFriend){
        if (friend == null) return;

        // go to user's friend list
        final CollectionReference collectionReference = this.userReference.document(user.getUid())
                .collection("FRIENDS");
        // remove the friend's user name from friend list (friends who user is following)
        collectionReference.document(friend.getUsername()).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Remove Friend " + friend.getUsername()
                                + " for " + user.getUsername());
                        if(toRemoveFriend) {
                            removeFollower(friend, user, false);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Did nothing.");
                    }
                });
    }

    /**
     *This method goes to user's follower list of a user and removes the follower's username if the boolean toRemoveFollower is true
     * @param user
     *  The user who wants to remove a follower
     * @param follower
     *  The follower who might be removed
     * @param toRemoveFollower
     *  The boolean that determines if a follower is removed or not
     */
    private void removeFollower(final MoodbookUser user, final MoodbookUser follower,
                               final boolean toRemoveFollower) {
        if (follower == null) return;
        final CollectionReference collectionReference = this.userReference.document(user.getUid())
                .collection("FOLLOWERS");
        collectionReference.document(follower.getUsername()).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Remove Follower " + follower.getUsername()
                                + " for " + user.getUsername());
                        if(toRemoveFollower) {
                            removeFriend(follower, user, false);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Did nothing");
                    }
                });
    }

    /**
     * This EventListener is for MyFriends & MyFollowers to get all the user's friends/followers
     * (username, uid) in the database from the user's friend/follower collection
     * @return a snapshot of the most updated username and uid in the user's friend/followers collection on firestore
     *
     */
    private EventListener<QuerySnapshot> getUserEventListener (){
        return new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @NonNull FirebaseFirestoreException e) {
                listListener.beforeGettingList();
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                    if (doc.getId().equals("null")) continue;
                    if (doc.getData() != null && doc.getData().get("uid") != null) {
                        MoodbookUser user = new MoodbookUser(
                                doc.getId(),
                                (String) doc.getData().get("uid"));
                        listListener.onGettingItem(user);
                    }
                }
                listListener.afterGettingList();
            }
        };
    }


    /**
     * This EventListener is for FriendMood and FriendMoodMap to get the most recent mood
     * from all the user's friends in the database starting from the user's friend collection
     * @return a snapshot of the most updated recent moods of all the  user's friend/followers on Firestore
     */
    private EventListener<QuerySnapshot> getFriendRecentMoodEventListener (){
        return new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @NonNull FirebaseFirestoreException e) {
                listListener.beforeGettingList();
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                    if (doc.getId().equals("null")) continue;
                    if (doc.getData() != null && doc.getData().get("uid") != null) {
                        MoodbookUser friendUser = new MoodbookUser(
                                doc.getId(),
                                (String) doc.getData().get("uid"));
                        getRecentMoodID(friendUser);
                    }
                }
                listListener.afterGettingList();
            }
        };
    }

    /**
     * This get recentMoodID in the database for a specific user
     * @param user
     *  The user that has the most recent mood
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
                                listListener.onGettingItem(friendMood);
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

}
