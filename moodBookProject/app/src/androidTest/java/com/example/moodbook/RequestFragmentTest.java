package com.example.moodbook;

import android.util.Log;
import android.widget.EditText;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.moodbook.ui.login.LoginActivity;
import com.robotium.solo.Solo;

import junit.framework.AssertionFailedError;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;


public class RequestFragmentTest {
    private Solo solo;

    @Rule
    public ActivityTestRule<LoginActivity> rule =
            new ActivityTestRule<>(LoginActivity.class, true, true);


    @Before
    public void setUp() throws Exception{
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
        // logout if logged in
        if (solo.searchText("Mood History")){
            solo.clickOnImageButton(0);
            solo.clickOnText("Logout");
            solo.sleep(3000);
        }
        // login with test account
        if (solo.searchText("login")){
            login();
        }
    }

    /**
     * used in tests to first login to the app
     */
    public void login(){
        solo.enterText((EditText) solo.getView(R.id.email), "test@test.com");
        solo.enterText((EditText) solo.getView(R.id.password), "testtest");
        solo.clickOnButton("login");
    }

    /**
     * Test for successful add request
     */
    // https://stackoverflow.com/questions/16596418/how-to-handle-exceptions-in-junit  - Eric Jablow    used to ignore failed login method
    //@Test(expected = junit.framework.AssertionFailedError.class) // ignore failed login attempt (if already logged in)
    @Test
    public void sendRequest(){
        //login();
        // navigate to request sending
        solo.clickOnImageButton(0);
        solo.clickOnText("Add Friends");

        solo.enterText((EditText) solo.getView(R.id.usernameEditText), "tyler");
        solo.clickOnButton("Send Request");
        assertTrue(solo.waitForText("Sent request"));
    }

    /**
     * Test for unsuccessful add request
     */
    @Test
    public void sendRequestInvalid(){
        //login();
        // navigate to request sending
        solo.clickOnImageButton(0);
        solo.clickOnText("Add Friends");

        solo.enterText((EditText) solo.getView(R.id.usernameEditText), "2j2ieh2@@@dwed33");
        solo.clickOnButton("Send Request");
        assertTrue(solo.waitForText("User does not exist"));
    }

    /**
     * Test for empty add request
     */
    @Test
    public void sendRequestEmpty(){
        //login();
        // navigate to request sending
        solo.clickOnImageButton(0);
        solo.clickOnText("Add Friends");

        solo.clickOnButton("Send Request");
        assertTrue(solo.waitForText("User does not exist"));
    }
}
