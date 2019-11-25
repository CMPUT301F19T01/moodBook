package com.example.moodbook;

import com.example.moodbook.ui.Request.RequestHandler;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class RequestHandlerTest {


    @Mock
    FirebaseAuth mockmAuth;

    @Mock
    FirebaseFirestore mockdb;

    private RequestHandler requestHandler;


    /**
     * nothing to test as the method is void and only interacts with db
     */
    @Test
    public void testSendRequest(){

    }

}
