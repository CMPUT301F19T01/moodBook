package com.example.moodbook;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.moodbook.ui.followers.followersAdapter;
import com.example.moodbook.ui.friendMood.FriendMood;
import com.example.moodbook.ui.friendMood.FriendMoodFragment;
import com.example.moodbook.ui.friendMood.FriendMoodListAdapter;
import com.example.moodbook.ui.myFriendMoodMap.MyFriendMoodMapFragment;
import com.example.moodbook.ui.myFriends.FriendListAdapter;
import com.example.moodbook.ui.myFriends.MyFriendsFragment;
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
    private DBListListener listListener;

    /**
     *
     * @param mAuth
     * @param context
     * @param TAG
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
     * Default TAG version
     * @param mAuth
     * @param context
     */
    public DBFriend(FirebaseAuth mAuth, Context context) {
        this(mAuth, context, DBFriend.class.getSimpleName());
    }

    /**
     * This is used by MyFriends
     * @param listListener
     */
    public void setFriendListListener(@NonNull DBListListener listListener) {
        this.listListener = listListener;
        this.userReference.document(uid).collection("FRIENDS")
                .addSnapshotListener(getFriendEventListener());
    }

    /**
     * This is used by followers fragment
     * @param followersListAdapter
     */
    public void setFollowersListListener(@NonNull followersAdapter followersListAdapter) {
        this.userReference.document(uid).collection("FOLLOWERS")
                .addSnapshotListener(getFollowerEventListener(followersListAdapter));
    }

    /**
     * This is used by FriendMood and FriendMoodMap
     * @param listListener
     */
    public void setFriendRecentMoodListener(@NonNull DBListListener listListener) {
        this.listListener = listListener;
        this.userReference.document(uid).collection("FRIENDS")
                .addSnapshotListener(getFriendRecentMoodEventListener());
    }

    /**
     * This set recentMoodID in the database for a user
     */
    public static void setRecentMoodID(FirebaseFirestore db, String currentUid, String recentMoodID) {
        DocumentReference currentUserDoc = db.collection("USERS").document(currentUid);
        currentUserDoc.update("recent_moodID", recentMoodID);
    }

    /**
     * This EventListener is for MyFollowers to get all the user's followers (username, uid) in the database
     * from the user's followers collection
     */
    private EventListener<QuerySnapshot> getFollowerEventListener (@NonNull final followersAdapter followersListAdapter){
        return new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @NonNull FirebaseFirestoreException e) {
                // clear the old list
                followersListAdapter.clear();
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                    // ignore null item
                    if (doc.getId().equals("null")) continue;
                    // Adding friend from FireStore
                    if (doc.getData() != null && doc.getData().get("uid") != null) {
                        MoodbookUser followerUser = new MoodbookUser(doc.getId(), (String) doc.getData().get("uid"));
                        followersListAdapter.add(followerUser);
                    }
                }
            }
        };

    }

    /**
     * This method is used by followersFragment to remove a follower who was previously accepted by the user
     * @param user
     * @param follower
     */
    public void removeFollower(final MoodbookUser user, final MoodbookUser follower) {
        removeFollower(user, follower, true);
    }

    private void removeFollower(final MoodbookUser user, final MoodbookUser follower,
                               final boolean toRemoveFriend) {
        if (follower == null) return;
        // go to user's follower list
        final CollectionReference collectionReference = this.userReference.document(user.getUid())
                .collection("FOLLOWERS");
        // remove the follower's user name from follower list (followers who are following user)
        collectionReference.document(follower.getUsername()).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Remove Follower " + follower.getUsername()
                                + " for " + user.getUsername());
                        if(toRemoveFriend) {
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
     * This method is MyFriendsFragment to remove a friend that the user follows
     * @param user
     * @param friend
     */
    public void removeFriend(final MoodbookUser user, final MoodbookUser friend) {
        removeFriend(user, friend, true);
    }

    private void removeFriend(final MoodbookUser user, final MoodbookUser friend,
                             final boolean toRemoveFollower){
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
                        if(toRemoveFollower) {
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
     * This EventListener is for MyFriends to get all the user's friends (username, uid) in the database
     * from the user's friend collection
     */
    private EventListener<QuerySnapshot> getFriendEventListener (){
        return new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @NonNull FirebaseFirestoreException e) {
                // clear the old list
                //friendListAdapter.clear();
                listListener.beforeGettingList();
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                    // ignore null item
                    if (doc.getId().equals("null")) continue;
                    // Adding friend from FireStore
                    if (doc.getData() != null && doc.getData().get("uid") != null) {
                        MoodbookUser friendUser = new MoodbookUser(doc.getId(), (String) doc.getData().get("uid"));
                        //friendListAdapter.add(friendUser);
                        listListener.onGettingItem(friendUser);
                    }
                }
                listListener.afterGettingList();
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
                listListener.beforeGettingList();
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
