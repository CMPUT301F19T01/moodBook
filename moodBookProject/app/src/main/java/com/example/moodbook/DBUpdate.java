package com.example.moodbook;

import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Interface that updates the database
 */
public interface DBUpdate {
    public void updateList(FirebaseFirestore db);
}
