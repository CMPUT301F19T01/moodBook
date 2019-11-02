package com.example.moodbook;

import android.widget.EditText;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.robotium.solo.Solo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;


public class RequestFragmentTest {
    private Solo solo;

    @Rule
    public ActivityTestRule<MainActivity> rule =
            new ActivityTestRule<>(MainActivity.class, true, true);


    @Before
    public void setUp() throws Exception{
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
    }

    /**
     * used in tests to first login to the app
     */
    public void login(){
        solo.enterText((EditText) solo.getView(R.id.email), "test@test.com");
        solo.enterText((EditText) solo.getView(R.id.password), "testtest");
        solo.clickOnButton("login");
        solo.sleep(5000); // wait for activity to change
    }

    /**
     * Tests login with test@test.com and password testtest
     */
    @Test
    public void sendRequest(){
        login();
        // navigate to request sending
        solo.clickOnActionBarHomeButton();
        //solo.clickOnMenuItem();

        solo.enterText((EditText) solo.getView(R.id.usernameEditText), "JIM");
        solo.clickOnButton("Send Request");
        solo.sleep(5000); // wait for activity to change
        assertTrue(solo.waitForText("Sent request"));
    }
}
