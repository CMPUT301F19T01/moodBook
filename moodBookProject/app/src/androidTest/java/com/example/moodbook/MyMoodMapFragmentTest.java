package com.example.moodbook;

import android.widget.TextView;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;

import com.example.moodbook.ui.login.LoginActivity;
import com.google.android.gms.maps.MapView;
import com.robotium.solo.Solo;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.HashMap;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.assertEquals;

/**
 * tests MyMoodMapFragment for map functionality and mood viewing
 */
public class MyMoodMapFragmentTest {
    private Solo solo;

    @Rule
    public ActivityTestRule<LoginActivity> rule = new ActivityTestRule<>(LoginActivity.class,
            true, true);

    @Before
    public void setUp(){
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
        TestHelper.setup(solo);
    }

    /**
     * helper method that adds a mood to the users mood history
     * @return
     *  returns hash map of mood attribute-value pairs
     */
    private HashMap<String, String> addMood(){
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
        solo.clickOnImageButton(0);
        solo.clickOnText("My MoodBook");
        return TestHelper.addMoodComplete(solo);
    }

    /**
     * Tests is map is ready and viewing moods on map
     */
    @Test
    public void testMap() throws UiObjectNotFoundException {
        HashMap<String,String> moodAttributes = addMood();
        String date = moodAttributes.get("date");
        String time = moodAttributes.get("time");

        // switch to mood map fragment
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
        solo.clickOnImageButton(0);
        solo.clickOnText("My Mood Map");
        solo.sleep(3000);

        MapView mapView = (MapView) solo.getView(R.id.mapView);

        // test if map is ready to be used
        assertEquals(  "Expected map view to be ready","MAP READY", mapView.getContentDescription());

        // test if map view is shown
        Assert.assertTrue("Expected mapView.shown() is true", mapView.isShown());

        // find marker on map view
        UiDevice device = UiDevice.getInstance(getInstrumentation());
        UiObject marker = device.findObject(new UiSelector().descriptionContains(date + " " + time));

        marker.click();

        // test emotion text
        TextView emotionText = (TextView) solo.getView(R.id.view_emotion);
        assertEquals(emotionText.getText().toString(), moodAttributes.get("emotion"));

        // test dateTime text
        TextView dateTimeText = (TextView) solo.getView(R.id.view_date_time);
        assertEquals(dateTimeText.getText().toString(), date + " at " + time);

    }
}
