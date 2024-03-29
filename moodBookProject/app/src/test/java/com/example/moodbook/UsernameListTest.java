package com.example.moodbook;

import com.example.moodbook.data.UsernameList;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.jupiter.api.Assertions.assertEquals;

/*
 * Unit testing methods in UsernameList
 */
@RunWith(MockitoJUnitRunner.class)
public class UsernameListTest {


    @Mock
    FirebaseFirestore mockdb;

    private final UsernameList usernameList = new UsernameList(mockdb);


    @Test
    public void testVerifyUsername(){
        // Test valid username
        String username = "validName";
        Boolean result = usernameList.verifyUsername(username);
        assertEquals(true, result);

        // Test invalid username
        username = "";
        result = usernameList.verifyUsername(username);
        assertEquals(false, result);

    }

    /**
     * nothing to test as method is void and only interacts with db
     */
    @Test
    public void testUpdateUsernameList(){

    }


}
