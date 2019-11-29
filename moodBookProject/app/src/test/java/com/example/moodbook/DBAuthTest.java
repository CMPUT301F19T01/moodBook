package com.example.moodbook;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.example.moodbook.ui.login.DBAuth;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.jupiter.api.Assertions.*;

/*
 * Unit testing methods inside DBAuth
 */
@RunWith(MockitoJUnitRunner.class)
public class DBAuthTest {

    private static final String mockString = "Mock String";

    @Mock
    FirebaseAuth mockmAuth;

    @Mock
    FirebaseFirestore mockdb;

    private final DBAuth dbAuth = new DBAuth(mockmAuth, mockdb);


    /**
     * This tests the verifyEmail method to ensure an email string is verified correctly
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

    /**
     * This tests the verifyPass method to ensure a password string is verified correctly
     */
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

    /**
     * nothing to test as it interacts with the db and has no state changes
     */
    @Test
    public void testCreateUser(){

    }

    /**
     * nothing to test as it interacts with Firebase and has no state changes
     */
    @Test
    public void testUpdateUsername(){

    }


}
