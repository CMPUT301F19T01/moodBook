package com.example.moodbook;

import android.widget.EditText;
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

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.HashMap;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * tests MyFriendMoodMapFragment for map functionality and mood viewing
 * Note: before running the test, tester must change the location of the emulator
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
     * helper method for setting up friends moods
     * @param solo
     * @return
     *  hash map of friends mood attributes
     */
    private HashMap<String, String> addFriendMood(Solo solo){
        // logout if logged in
        if (solo.searchText("Mood History")){
            solo.clickOnImageButton(0);
            for (int i = 0; i < 4; i++){
                solo.sendKey(Solo.DOWN);
            }
            TestHelper.scrollToLogout(solo);
            solo.sleep(3000);
        }
        // login with test account
        if (solo.searchText("login")){
            solo.enterText((EditText) solo.getView(R.id.email), "newtest@test.com");
            solo.enterText((EditText) solo.getView(R.id.password), "testtest");
            solo.clickOnButton("login");
        }

        HashMap<String, String> moodHashMap = TestHelper.addMoodComplete(solo);

        TestHelper.setup(solo);

        return moodHashMap;

    }

    /**
     * Test if map is ready and viewing moods on map
     */
    @Test
    public void test() throws UiObjectNotFoundException {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
        HashMap<String, String>  friendMoodAttributes = addFriendMood(solo);
        String date = friendMoodAttributes.get("date");
        String time = friendMoodAttributes.get("time");

        // switch to mood map fragment
        solo.clickOnImageButton(0);
        solo.clickOnText("Friend Mood Map");
        solo.sleep(3000);

        // find map
        MapView mapView = (MapView) solo.getView(R.id.friendMapView);

        // test if map is ready to be used
        assertEquals(  "Expected map view to be ready","MAP READY", mapView.getContentDescription());

        // test if map view is shown
        assertTrue("Expected mapView.shown() is true", mapView.isShown());

        // find marker on map view
        UiDevice device = UiDevice.getInstance(getInstrumentation());
        UiObject marker = device.findObject(new UiSelector().descriptionContains(date + " " + time));

        marker.click();

        // test emotion text
        TextView emotionText = (TextView) solo.getView(R.id.view_emotion);
        assertEquals(emotionText.getText().toString(), friendMoodAttributes.get("emotion"));

        // test dateTime text
        TextView dateTimeText = (TextView) solo.getView(R.id.view_date_time);
        assertEquals(dateTimeText.getText().toString(), date + " at " + time);

    }

}
