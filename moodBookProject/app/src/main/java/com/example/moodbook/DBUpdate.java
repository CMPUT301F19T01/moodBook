package com.example.moodbook;

import com.example.moodbook.ui.myMoodMap.MyMoodMapFragment;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * DBUpdate.java
 * Interface class that has the updateList method for interaction with Firebase
 * @author Neilzon Viloria
 * @since 07-11-2019

 * This class contains generalized methods used by CreateMoodActivity and EditMoodActivity
 * @see MyMoodMapFragment
 */
public interface DBUpdate {
    // get moods from Firebase into a list
    public void updateList(FirebaseFirestore db);
}
