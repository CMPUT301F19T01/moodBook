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

import org.junit.Assert;
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
        public void setUp() {
            solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
            TestHelper.setup(solo);
        }

        /**
         * Clicks on the Fab button for adding moods to go to createMood Activity
         */
        @Test
        public void CreateActivityTest() {
            // wait for activity to change
            solo.sleep(5000);
            solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
            // ensure current fragment is for Mood History
            Assert.assertTrue(solo.searchText("Mood History"));

            solo.clickOnView(solo.getView(R.id.mood_history_add_button));
            solo.sleep(5000); // wait for activity to change
            assertTrue(solo.waitForActivity(CreateMoodActivity.class));
        }

        /**
         * Tests the adding of location
         * NOTE: Set location to 37.4220, -122.0840 in the android emulator
         */
        @Test
        public void addLocationTest () {
            // wait for activity to change
            solo.sleep(5000);
            solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
            // ensure current fragment is for Mood History
            Assert.assertTrue(solo.searchText("Mood History"));

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

    }

