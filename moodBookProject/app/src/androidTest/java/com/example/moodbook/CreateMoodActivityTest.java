package com.example.moodbook;

import android.widget.EditText;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.moodbook.ui.home.CreateMoodActivity;
import com.example.moodbook.ui.login.LoginActivity;
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Button;

import com.google.android.gms.maps.model.LatLng;
import com.robotium.solo.Solo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static junit.framework.TestCase.assertTrue;
import androidx.core.app.ActivityCompat;

import static org.junit.Assert.assertEquals;

/**
 * Test class for CreateMoodActivity. All the UI tests are written here.
 * Robotium test framework is used
 * Test class for CreateMoodActivity. All the UI tests are written here. Robotium test framework is
 used
 * todo:
 *  * write tests for location picker
 */
public class CreateMoodActivityTest {

        private Solo solo;

        // test location
        private LatLng pickedLocation;

        @Rule
        public ActivityTestRule<LoginActivity> rule = new ActivityTestRule<>(LoginActivity.class, true, true);

        @Before
        public void setUp () throws Exception {
            solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());


            // logout if logged in
            if (solo.searchText("Mood History")) {
                solo.clickOnImageButton(0);
                for (int i = 0; i < 4; i++){
                    solo.sendKey(Solo.DOWN);
                }
                solo.clickOnText("Logout");
                solo.sleep(3000);
            }
            // login with test account
            if (solo.searchText("login")) {
                login();
            }

        }

        public void login () {
            solo.enterText((EditText) solo.getView(R.id.email), "test@test.com");
            solo.enterText((EditText) solo.getView(R.id.password), "testtest");
            solo.clickOnButton("login");
        }

//    /**
//     * used in tests to first login to the app
//     */
//    public void login(){
//        solo.enterText((EditText) solo.getView(R.id.email), "kathleen@gmail.com");
//        solo.enterText((EditText) solo.getView(R.id.password), "testing");
//        solo.clickOnButton("login");
//    }


    /*
    @Test
    public void start() throws Exception {

    }
*/
        /**
         * Clicks on the Fab button for adding moods to go to createMood Activity
         */
        @Test
        public void CreateActivityTest () {
            solo.clickOnView(solo.getView(R.id.mood_history_add_button));
            solo.sleep(5000); // wait for activity to change
            assertTrue(solo.waitForActivity(CreateMoodActivity.class));
        }

        public void clickAdd () {
            solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
            solo.clickOnImageButton(0);
            solo.clickOnText("My MoodBook");
            solo.sleep(3000);


            solo.clickOnView(solo.getView(R.id.mood_history_add_button));
            solo.sleep(5000); // wait for activity to change
        }


        /**
         * Tests the adding of location
         * NOTE: Set location to 37.4220, -122.0840 in the android emulator
         */
        @Test
        public void addLocationTest () {
            solo.clickOnImageButton(0);
            solo.clickOnText("My MoodBook");
            solo.sleep(3000);

            // create mood
            solo.clickOnView(solo.getView(R.id.mood_history_add_button));

            // add location by clicking on button
            solo.clickOnView(solo.getView(R.id.create_location_button));

            // wait for activity to launch
            solo.sleep(3000);

            // check if we are on LocationPickerActivity
            solo.assertCurrentActivity("Expected Maps Activity to launch", LocationPickerActivity.class);

            // add location
            solo.clickOnView(solo.getView(R.id.confirmButton));
            solo.sleep(2000);

            // check if we got back to CreateMoodActivity
            solo.assertCurrentActivity("Expected create mood activity to launch", CreateMoodActivity.class);

            // check the coordinates passed back and set into the pick location button text
            Button pickLocationButton = (Button) solo.getView(R.id.create_location_button);
            String actual = pickLocationButton.getText().toString();


            String expected = "1600 Amphitheatre Pkwy, Mountain View, CA 94043, USA";

            assertEquals("Expected coords returned from LocationPickerActivity to match users location",
                    expected,
                    actual);
        }

    /*
  @Test
    public void addMood(){
        solo.clickOnView(solo.getView(R.id.create_date_button)); //date button
        solo.clickOnView(solo.getView(R.id.create_time_button)); //time button
        solo.clickOnView(solo.getView(R.id.create_location_button)); //time button

        solo.clickOnView(solo.getView(R.id.create_emotion_spinner, 0));//emotion --Picks alone
        solo.clickOnView(solo.getView(R.id.create_location_button));
        solo.clickOnView(solo.getView(R.id.create_emotion_spinner, 0));

        solo.clickOnView(solo.getView(R.id.create_add_button)); //Select CONFIRM Button

    }
    */

    }

