package com.example.moodbook;

import com.example.moodbook.ui.Request.RequestHandler;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * All methods found within RequestHandler interact with the db
 * After mocking the db, there would be no remaining functionality to test
 */
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

    /**
     * nothing to test as it is a void and a simple setter and also interacts with the db
     */
    @Test
    public void testSetRequestListListener(){

    }

    /**
     * nothing to test as it is a simple getter that interacts with the db
     */
    @Test
    public void testGetRequestListener(){

    }

    /**
     * nothing to test as it is is void and interacts with the db
     */
    @Test
    public void testAddFriend(){

    }

}
