package com.example.moodbook;

import android.content.Intent;
import android.widget.EditText;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.moodbook.ui.login.LoginActivity;
import com.example.moodbook.ui.login.RegisterActivity;
import com.robotium.solo.Solo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Random;

import static junit.framework.TestCase.assertTrue;


public class RegisterActivityTest {
    private Solo solo;

    @Rule
    public ActivityTestRule<LoginActivity> rule =
            new ActivityTestRule<>(LoginActivity.class, true, true);

    // https://stackoverflow.com/questions/20536566/creating-a-random-string-with-a-z-and-0-9-in-java   - Suresh Atta     used to generate unique username and email address
    private String getSaltString() {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 18) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;

    }

    @Before
    public void setUp() throws Exception{
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
        // logout if logged in
        if (solo.searchText("Mood History")){
            solo.clickOnImageButton(0);
            solo.clickOnText("Logout");
            solo.sleep(3000);
        }
        solo.clickOnText("register");
        solo.sleep(3000);
    }

    /**
     * Tests register
     */
    @Test
    public void registerSucceed(){
        String unique = getSaltString();
        String email = unique + "@test.com";
        solo.enterText((EditText) solo.getView(R.id.email), email);
        solo.enterText((EditText) solo.getView(R.id.username), unique);
        solo.enterText((EditText) solo.getView(R.id.password), "testtest");
        solo.clickOnButton("register");
        solo.sleep(5000); // wait for activity to change
        assertTrue(solo.waitForText("Mood History"));

    }

    /**
     * Tests register for a failed register due to email
     */
    @Test
    public void registerFailEmail(){
        // invalid email
        solo.enterText((EditText) solo.getView(R.id.email), "fail");
        solo.enterText((EditText) solo.getView(R.id.password), "testtest");
        solo.enterText((EditText) solo.getView(R.id.username), "unusedtestuser");
        solo.clickOnButton("register");
        assertTrue(solo.waitForText("Incorrect email format"));

    }

    /**
     * Tests register for a failed register due to password
     */
    @Test
    public void registerFailPassword(){
        // invalid email
        solo.enterText((EditText) solo.getView(R.id.email), "test@gmail.com");
        solo.enterText((EditText) solo.getView(R.id.password), "fail");
        solo.enterText((EditText) solo.getView(R.id.username), "unusedtestuser");
        solo.clickOnButton("register");

        assertTrue(solo.waitForText("Password must be"));
    }

    /**
     * Tests register for a failed register due to username taken
     */
    @Test
    public void registerFailUsername(){
        // invalid email
        solo.enterText((EditText) solo.getView(R.id.email), "test@gmail.com");
        solo.enterText((EditText) solo.getView(R.id.password), "testtest");
        solo.enterText((EditText) solo.getView(R.id.username), "test");
        solo.clickOnButton("register");

        assertTrue(solo.waitForText("Username in use"));
    }

    /**
     * Tests register for a failed register due to empty email
     */
    @Test
    public void registerFailEmptyEmail(){
        // invalid email
        solo.enterText((EditText) solo.getView(R.id.email), "");
        solo.enterText((EditText) solo.getView(R.id.password), "testtest");
        solo.enterText((EditText) solo.getView(R.id.username), "unusedtestuser");
        solo.clickOnButton("register");

        assertTrue(solo.waitForText("Incorrect email format"));
    }

    /**
     * Tests register for a failed register due to empty username
     */
    @Test
    public void registerFailEmptyUsername(){
        // invalid email
        solo.enterText((EditText) solo.getView(R.id.email), "test@gmail.com");
        solo.enterText((EditText) solo.getView(R.id.password), "testtest");
        solo.enterText((EditText) solo.getView(R.id.username), "");
        solo.clickOnButton("register");

        assertTrue(solo.waitForText("Username in use"));
    }

    /**
     * Tests register for a failed register due to empty password
     */
    @Test
    public void registerFailEmptyPassword(){
        // invalid email
        solo.enterText((EditText) solo.getView(R.id.email), "test@gmail.com");
        solo.enterText((EditText) solo.getView(R.id.password), "");
        solo.enterText((EditText) solo.getView(R.id.username), "unusedtestuser");
        solo.clickOnButton("register");

        assertTrue(solo.waitForText("Password must be"));
    }

}
