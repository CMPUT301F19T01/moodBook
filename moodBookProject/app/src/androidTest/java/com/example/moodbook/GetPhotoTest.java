package com.example.moodbook;

import android.widget.EditText;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.moodbook.ui.home.CreateMoodActivity;
import com.example.moodbook.ui.login.LoginActivity;
import com.robotium.solo.Solo;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static junit.framework.TestCase.assertTrue;

    public class GetPhotoTest {
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
         * Test for Gallery intent
         * Ensure the emulator has a photo in its gallery before starting
         */
        @Test
        public void GalleryTest(){
            // wait for activity to change
            solo.sleep(5000);
            solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
            // ensure current fragment is for Mood History
            Assert.assertTrue(solo.searchText("Mood History"));

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

