package com.example.moodbook;

import android.app.Fragment;
import android.util.Log;
import android.widget.EditText;

import androidx.test.internal.runner.listener.InstrumentationRunListener;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.moodbook.ui.login.LoginActivity;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.robotium.solo.Solo;

import junit.framework.AssertionFailedError;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * todo: write tests for
 *  * markers with expected location data
 *  * markers with no location data
 *  * delete location
 */
public class MyMoodMapFragmentTest {
    private Solo solo;

    @Rule
    public ActivityTestRule<LoginActivity> rule = new ActivityTestRule<>(LoginActivity.class, true, true);
    @Rule
    public ActivityTestRule<MainActivity> rule2 = new ActivityTestRule<>(MainActivity.class, true, true);

    @Before
    public void setUp() throws Exception {
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

    public void login(){
        solo.enterText((EditText) solo.getView(R.id.email), "maptesting@maptesting.com");
        solo.enterText((EditText) solo.getView(R.id.password), "password");
        solo.clickOnButton("login");
    }


    public void addMood(){
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
        solo.clickOnImageButton(0);
        solo.clickOnText("My MoodBook");

        solo.clickOnView(solo.getView(R.id.mood_history_add_button));
        solo.sleep(5000); // wait for activity to change

        CreateMoodActivity createMoodActivity = (CreateMoodActivity) solo.getCurrentActivity();

        // new mood
        solo.clickOnView(solo.getView(R.id.create_emotion_spinner, 0));
        solo.clickOnView(solo.getView(R.id.create_date_button)); //date button
        solo.clickOnView(solo.getView(R.id.create_time_button)); //time button
        solo.clickOnView(solo.getView(R.id.create_location_button)); //time button

        solo.clickOnView(solo.getView(R.id.create_add_button)); //Select CONFIRM Button

    }

    public void deleteMood(){
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule2.getActivity());
        solo.clickOnImageButton(0);
        solo.clickOnText("My MoodBook");


        solo.clickOnView(solo.getView(R.id.mood_history_add_button));
        solo.sleep(5000); // wait for activity to change
    }


    /**
     * Test if map is loaded and shown
     */
    @Test
    public void test(){
        // switch to mood map fragment
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule2.getActivity());
        solo.clickOnImageButton(0);
        solo.clickOnText("My Mood Map");
        solo.sleep(30000);

        MapView mapView = (MapView) solo.getView(R.id.mapView);
        // test if map is ready to be used
        assertEquals(  "Expected map view to be ready","MAP READY", mapView.getContentDescription());

        // test if map view is shown
        assertEquals("Expected mapView.shown() is true",true, mapView.isShown());

    }

    /**
     * Test for drawing marker
     */
    @Test
    public void drawMarkerTest(){

    }

}
