package com.example.moodbook;

import com.google.firebase.firestore.FirebaseFirestore;

public interface DBUpdate {
    public void updateList(FirebaseFirestore db);
}
