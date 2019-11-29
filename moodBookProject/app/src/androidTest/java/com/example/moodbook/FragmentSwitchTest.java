package com.example.moodbook;

import android.widget.EditText;

import com.example.moodbook.ui.login.LoginActivity;
import com.robotium.solo.Solo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import androidx.fragment.app.Fragment;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Tests ability to access various fragments through the mrenu
 */
public class FragmentSwitchTest {
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
            for (int i = 0; i < 4; i++){
                solo.sendKey(Solo.DOWN);
            }
            solo.clickOnText("Logout");
            solo.sleep(2000);
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
    /*Testing Friend Mood Book*/
    @Test
    public void FragmentSwitchFriendMoodBook() {
        solo.clickOnImageButton(0);
        solo.clickOnText("Friend MoodBook");
        solo.sleep(2000);
        assertTrue(solo.waitForText("Friend MoodBook"));
    }
    /*Testing Add Friend */

    @Test
    public void FragmentSwitchAddFriends() {
        solo.clickOnImageButton(0);
        for (int i = 0; i < 4; i++){
            solo.sendKey(Solo.DOWN);
        }
        solo.clickOnText("Add Friends");
        solo.sleep(2000);
        assertTrue(solo.waitForText("Add Friends"));
    }
    /*Testing Friend My Requests*/

    @Test
    public void FragmentSwitchMyRequests() {
        solo.clickOnImageButton(0);
        for (int i = 0; i < 4; i++){
            solo.sendKey(Solo.DOWN);
        }
        solo.clickOnText("My Requests");
        solo.sleep(2000);
        assertTrue(solo.waitForText("Friend Requests"));
    }
//    @Test
//    public void FragmentSwitchMyMoodMap() {
//        solo.clickOnImageButton(0);
//        solo.clickOnText("My Mood Map");
//        solo.sleep(5000);
//        assertTrue(solo.waitForText("Mood History Map"));
//    }
    /*Testing Friend Mood Map*/

    @Test
    public void FragmentSwitchFriendMoodMap() {
        solo.clickOnImageButton(0);
        solo.clickOnText("Friend Mood Map");
        solo.sleep(2000);
        assertTrue(solo.waitForText("Friend Mood Map"));
        solo.sleep(2000);

    }

    /*Testing Logout*/
    @Test
    public void FragmentSwitchLogout(){
        solo.clickOnImageButton(0);
        for (int i = 0; i < 4; i++){
            solo.sendKey(Solo.DOWN);
        }
        solo.clickOnText("Logout");
        solo.sleep(2000);
        assertTrue(solo.waitForText("Email"));
    }
}
