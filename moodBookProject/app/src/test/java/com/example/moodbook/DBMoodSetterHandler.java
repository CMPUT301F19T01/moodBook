package com.example.moodbook;

import android.content.Context;
import android.graphics.Bitmap;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class DBMoodSetterHandler {

    @Mock
    FirebaseAuth mockmAuth;

    @Mock
    Context context;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private static FirebaseStorage storage;
    private CollectionReference userReference;
    private CollectionReference intReference;
    private StorageReference photoReference;
    private DocumentReference intRef;
    private String uid;
    private String TAG;         // optional: for log message
    private String moodID;
    private Bitmap obtainedImg;
    EventListener moodHistoryListener;


    private final DBMoodSetter DBHandler = new DBMoodSetter(mockmAuth, context);


    private final DBMoodSetter DBHandler1 = new DBMoodSetter(mockmAuth, context, moodHistoryListener);


    /**
     * This test the verifyEmail method to
     */
    @Test
    public void testDBUpdate(){

    }



}
