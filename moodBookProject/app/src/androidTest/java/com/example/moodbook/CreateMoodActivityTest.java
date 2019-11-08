package com.example.moodbook;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.example.moodbook.ui.login.LoginActivity;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.robotium.solo.Solo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import androidx.core.app.ActivityCompat;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import static org.junit.Assert.assertEquals;

/**
 * Test class for CreateMoodActivity. All the UI tests are written here. Robotium test framework is
 used
 * todo:
 *  * write tests for location picker
 */
public class CreateMoodActivityTest {

    private Solo solo;
    private Solo solo2;

    // test location
    private LatLng pickedLocation;

    @Rule
    public ActivityTestRule<LoginActivity> rule = new ActivityTestRule<>(LoginActivity.class, true, true);
    @Rule
    public  ActivityTestRule<CreateMoodActivity> rule2 = new ActivityTestRule<>(CreateMoodActivity.class,true,true);

    @Before
    public void setUp() throws Exception {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
        solo2 = new Solo(InstrumentationRegistry.getInstrumentation(),rule2.getActivity());
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

    public void login(){
        solo.enterText((EditText) solo.getView(R.id.email), "hello@hello.com");
        solo.enterText((EditText) solo.getView(R.id.password), "password");
        solo.clickOnButton("login");
    }

    /**
     * Gets the Activity
     * @throws Exception
     */
    @Test
    public void start() throws Exception {

    }

    /**
     * Clicks on the Fab button for adding moods to go to createMood Activity
     */
    @Test
    public void clickAdd() {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
        solo.clickOnImageButton(0);
        solo.clickOnText("My MoodBook");
        solo.sleep(3000);


        solo.clickOnView(solo.getView(R.id.mood_history_add_button));
        solo.sleep(5000); // wait for activity to change
    }

    // get the current location to check against location picker
    public void getLocation(){
        // go to create mood activity


        LocationManager locationManager = (LocationManager) solo.getCurrentActivity().getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                // get current location and draw it on map as initial location
                pickedLocation = new LatLng(location.getLatitude(), location.getLongitude());
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {}

            @Override
            public void onProviderEnabled(String s) {}

            @Override
            public void onProviderDisabled(String s) {}
        };

        // request for update
        locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, locationListener, null);

        if (ActivityCompat.checkSelfPermission(solo.getCurrentActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(solo.getCurrentActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(solo.getCurrentActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
            return;
        }


    }

    /**
     * Tests the adding of location
     * set
     */

    @Test
    public void addLocationTest(){
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
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
        solo.sleep(70000);

        // check if we got back to CreateMoodActivity
        solo.assertCurrentActivity("Expected create mood activity to launch", CreateMoodActivity.class);

        // check the coordinates passed back and set into the pick location button text
        Button pickLocationButton = (Button) solo.getView(R.id.create_location_button);
        String actual = pickLocationButton.getText().toString();
        getLocation();

        String expected = String.valueOf(pickedLocation.latitude) + " , " + String.valueOf(pickedLocation.longitude);

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
