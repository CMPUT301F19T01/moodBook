package com.example.moodbook;

import com.example.moodbook.ui.login.DBAuth;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Test;
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

    private final DBAuth dbAuth = new DBAuth(mockmAuth, mockdb);


    /**
     * This test the verifyEmail method to
     */
    @Test
    public void testVerifyEmail(){
        // Test valid email
        String email = "testEmail@gmail.com";
        Boolean result = dbAuth.verifyEmail(email);
        assertEquals(true, result);

        // Test invalid email
        email = "testEmail";
        result = dbAuth.verifyEmail(email);
        assertEquals(false, result);

    }

    @Test
    public void testVerifyPass(){
        // Test valid password
        String password = "securepassword";
        Boolean result = dbAuth.verifyPass(password);
        assertEquals(true, result);

        // Test invalid password < 6 chars
        password = "12345";
        result = dbAuth.verifyPass(password);
        assertEquals(false, result);
    }

    @Test
    public void testCreateUser(){

    }@Test
    public void testUpdateUser(){

    }
}
