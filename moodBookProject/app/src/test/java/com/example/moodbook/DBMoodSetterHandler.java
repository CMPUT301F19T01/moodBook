package com.example.moodbook;

import android.content.Context;

import com.example.moodbook.ui.Request.RequestHandler;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

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

    private final DBMoodSetter DBHandler = new DBMoodSetter(mockmAuth, context);


    /**
     * This test the verifyEmail method to
     */
    @Test
    public void testSendRequest(){

    }



}
