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

    public class CameraTest {

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
            solo.enterText((EditText) solo.getView(R.id.email), "kathleen@gmail.com");
            solo.enterText((EditText) solo.getView(R.id.password), "testing");
            solo.clickOnButton("login");
            solo.sleep(5000);
        }

        /**
         * Tests that Camera intent
         */
        @Test
        public void CameraTest(){
            solo.clickOnView(solo.getView(R.id.mood_history_add_button));
            solo.sleep(5000); // wait for activity to change
            solo.clickOnView(solo.getView(R.id.create_date_button)); //date button
            solo.clickOnView(solo.getView(R.id.create_time_button)); //time button
            solo.clickOnView(solo.getView(R.id.create_emotion_spinner));//emotion --Picks alone
            solo.pressSpinnerItem(0,1);
            solo.clickOnView(solo.getView(R.id.create_reason_photo_button));
            solo.clickOnText("Capture photo from camera");
            assertTrue(solo.waitForLogMessage("Camera intent successful"));
        }

    }

