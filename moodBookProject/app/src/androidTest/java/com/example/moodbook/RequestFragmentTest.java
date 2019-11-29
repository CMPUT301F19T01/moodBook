// Reference: https://stackoverflow.com/questions/16596418/how-to-handle-exceptions-in-junit  - Eric Jablow    used to ignore failed login method

package com.example.moodbook;

import android.widget.EditText;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.moodbook.ui.login.LoginActivity;
import com.robotium.solo.Solo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static junit.framework.TestCase.assertTrue;


public class RequestFragmentTest {
    private Solo solo;

    @Rule
    public ActivityTestRule<LoginActivity> rule =
            new ActivityTestRule<>(LoginActivity.class, true, true);

    @Before
    public void setUp() {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
        TestHelper.setup(solo);
    }

    /**
     * Test for successful add request
     */
    //@Test(expected = junit.framework.AssertionFailedError.class) // ignore failed login attempt (if already logged in)
    @Test
    public void sendRequest(){
        //login();
        // navigate to request sending
        solo.clickOnImageButton(0);
        solo.clickOnText("Add Friends");

        solo.enterText((EditText) solo.getView(R.id.send_request_username_text), "tyler");
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

        solo.enterText((EditText) solo.getView(R.id.send_request_username_text), "2j2ieh2@@@dwed33");
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

    /**
     * Test for adding self
     */
    @Test
    public void sendRequestSelf(){
        //login();
        // navigate to request sending
        solo.clickOnImageButton(0);
        solo.clickOnText("Add Friends");
        solo.enterText((EditText) solo.getView(R.id.send_request_username_text), "test");
        solo.clickOnButton("Send Request");
        assertTrue(solo.waitForText("Cannot add yourself"));
    }

    /**
     * Test for adding a friend thats already added
     */
    @Test
    public void sendRequestAdded(){
        //login();
        // navigate to request sending
        solo.clickOnImageButton(0);
        solo.clickOnText("Add Friends");
        solo.enterText((EditText) solo.getView(R.id.send_request_username_text), "newtest");
        solo.clickOnButton("Send Request");
        assertTrue(solo.waitForText("User already added"));
    }
}
