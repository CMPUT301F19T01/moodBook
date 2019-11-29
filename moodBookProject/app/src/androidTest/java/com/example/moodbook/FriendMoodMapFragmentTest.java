package com.example.moodbook;

import android.app.Activity;
import android.widget.EditText;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;

import com.example.moodbook.ui.friendMood.FriendMood;
import com.example.moodbook.ui.login.LoginActivity;
import com.example.moodbook.ui.myFriendMoodMap.MyFriendMoodMapFragment;
import com.google.android.gms.maps.MapView;
import com.robotium.solo.Solo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;


import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * tests MyFriendMoodMapFragment for map functionality and mood viewing
 */
public class FriendMoodMapFragmentTest {
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
     * Test if map is ready and viewing moods on map
     */
    @Test
    public void test() throws UiObjectNotFoundException {
        // switch to mood map fragment
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
        solo.clickOnImageButton(0);
        solo.clickOnText("Friend Mood Map");
        solo.sleep(3000);

        // find map
        MapView mapView = (MapView) solo.getView(R.id.mapView);

        // test if map is ready to be used
        assertEquals(  "Expected map view to be ready","MAP READY", mapView.getContentDescription());

        // test if map view is shown
        assertTrue("Expected mapView.shown() is true", mapView.isShown());

        // find marker on map view
        UiDevice device = UiDevice.getInstance(getInstrumentation());
        UiObject marker = device.findObject(new UiSelector().descriptionContains("2019-11-28" + " " + "17:29:54"));

        marker.click();

    }

}
