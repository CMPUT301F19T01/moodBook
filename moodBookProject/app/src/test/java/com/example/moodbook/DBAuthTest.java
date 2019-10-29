package com.example.moodbook;

import com.example.moodbook.ui.login.DBAuth;
import com.google.firebase.auth.FirebaseAuth;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DBAuthTest {

    private DBAuth mockDBAuth(){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        return new DBAuth(mAuth);
    }

    @Test
    void testVerifyEmail(){
        DBAuth dbAuth = mockDBAuth();
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
