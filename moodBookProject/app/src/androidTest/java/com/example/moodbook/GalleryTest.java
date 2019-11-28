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

public class GalleryTest {


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
        solo.sleep(2000);
    }

    /**
     * Test for Gallery intent
     */
    @Test 
    public void GalleryTest(){
        solo.clickOnView(solo.getView(R.id.mood_history_add_button));
        solo.sleep(2000); // wait for activity to change
        solo.clickOnView(solo.getView(R.id.create_date_button)); //date button
        solo.clickOnView(solo.getView(R.id.create_time_button)); //time button
        solo.clickOnView(solo.getView(R.id.create_emotion_spinner));//emotion --Picks alone
        solo.pressSpinnerItem(0,1);
        solo.clickOnView(solo.getView(R.id.create_reason_photo_button));
        solo.clickOnText("Select photo from gallery");
        assertTrue(solo.waitForLogMessage("Gallery intent successful"));

    }
}
