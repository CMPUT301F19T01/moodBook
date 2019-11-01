package com.example.moodbook;

import com.example.moodbook.ui.login.DBAuth;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(MockitoJUnitRunner.class)
public class DBAuthTest {

    private static final String mockString = "Mock String";

    @Mock
    FirebaseAuth mockmAuth;
    //when(mockmAuth.signInWithEmailAndPassword(mockString, mockString));

    @Mock
    FirebaseFirestore mockdb;


    @Test
    void testVerifyEmail(){
        when(mockdb.collection(mockString)).then(Mockito.doNothing());
        DBAuth dbAuth = new DBAuth(mockmAuth, mockdb);

        // Test valid email
        String email = "testemail@gmail.com";
        Boolean result = dbAuth.verifyEmail(email);
        assertEquals(true, result);

        // Test invalid email
        email = "testemail";
        result = dbAuth.verifyEmail(email);
        assertEquals(false, result);

    }
}
