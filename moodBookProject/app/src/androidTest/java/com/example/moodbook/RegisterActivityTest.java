package com.example.moodbook;

import android.widget.EditText;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.moodbook.ui.login.LoginActivity;
import com.example.moodbook.ui.login.RegisterActivity;
import com.robotium.solo.Solo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static junit.framework.TestCase.assertTrue;


public class RegisterActivityTest {
    private Solo solo;

    @Rule
    public ActivityTestRule<RegisterActivity> rule =
            new ActivityTestRule<>(RegisterActivity.class, true, true);


    @Before
    public void setUp() throws Exception{
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
        // logout if logged in
        if (solo.searchText("Mood History")){
            solo.clickOnImageButton(0);
            solo.clickOnText("Logout");
        }
        solo.sleep(3000);
    }

    /**
     * Tests login with test@test.com and password testtest for successful login
     */
    @Test
    public void loginSucceed(){
        solo.enterText((EditText) solo.getView(R.id.email), "test@test.com");
        solo.enterText((EditText) solo.getView(R.id.password), "testtest");
        solo.clickOnButton("login");
        solo.sleep(5000); // wait for activity to change

        solo.assertCurrentActivity("Wrong activity", MainActivity.class);

    }

    /**
     * Tests login for a failed login due to email
     */
    @Test
    public void loginFailEmail(){
        // invalid email
        solo.enterText((EditText) solo.getView(R.id.email), "fail@test.com");
        solo.enterText((EditText) solo.getView(R.id.password), "testtest");
        solo.clickOnButton("login");

        assertTrue(solo.waitForText("Authentication failed"));

    }
    /**
     * Tests login for a failed login due to password
     */
    @Test
    public void loginFailPassword(){
        // invalid password
        solo.enterText((EditText) solo.getView(R.id.email), "test@test.com");
        solo.enterText((EditText) solo.getView(R.id.password), "failfail");
        solo.clickOnButton("login");

        assertTrue(solo.waitForText("Authentication failed"));

    }

    /**
     * Tests login for a failed login with empty email
     */
    @Test
    public void loginFailEmptyEmail(){
        // empty email
        solo.enterText((EditText) solo.getView(R.id.email), "");
        solo.enterText((EditText) solo.getView(R.id.password), "testtest");
        solo.clickOnButton("login");

        assertTrue(solo.waitForText("Incorrect email format"));

    }

    /**
     * Tests login for a failed login with empty password
     */
    @Test
    public void loginFailEmptyPassword(){
        // empty password
        solo.enterText((EditText) solo.getView(R.id.email), "test@test.com");
        solo.enterText((EditText) solo.getView(R.id.password), "");
        solo.clickOnButton("login");

        assertTrue(solo.waitForText("Password must be"));

    }
}
