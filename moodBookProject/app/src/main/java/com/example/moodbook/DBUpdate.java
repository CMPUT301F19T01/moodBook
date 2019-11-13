package com.example.moodbook;

import com.example.moodbook.ui.myMoodMap.MyMoodMapFragment;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Interface that updates the database
 * DBUpdate.java
 * Interface class that has the updateList method for interaction with Firebase
 * @author Neilzon Viloria
 * @since 07-11-2019

 * This class contains generalized methods used by MyMoodMapFragment and MyFriendMoodMapFragment
 * @see MyMoodMapFragment
 */
public interface DBUpdate {
    // get moods from Firebase into a list
    public void updateList(FirebaseFirestore db);
}
