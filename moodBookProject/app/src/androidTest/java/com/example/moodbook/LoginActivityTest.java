package com.example.moodbook;

import android.widget.EditText;

import com.example.moodbook.ui.login.LoginActivity;
import com.example.moodbook.ui.login.RegisterActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.robotium.solo.Solo;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * This tests the login activity
 */
public class LoginActivityTest {
    private Solo solo;

    @Rule
    public ActivityTestRule<LoginActivity> rule =
            new ActivityTestRule<>(LoginActivity.class, true, true);

    @Before
    public void setUp() {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
        if (solo.searchText("Mood History")){
            solo.clickOnImageButton(0);
            for (int i = 0; i < 4; i++){
                solo.sendKey(Solo.DOWN);
            }
            TestHelper.scrollToLogout(solo);
            solo.sleep(3000);
        }
    }

    /**
     * Tests login with premade test@test.com and password testtest for successful login
     */
    @Test
    public void loginSucceed(){
        TestHelper.login(solo);

        // wait for activity to change
        solo.sleep(5000);
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

    /**
     * Tests register button
     */
    @Test
    public void loginRegister(){
        solo.clickOnButton("register");

        // wait for activity to change
        solo.sleep(5000);
        solo.assertCurrentActivity("Wrong activity", RegisterActivity.class);
    }
}
