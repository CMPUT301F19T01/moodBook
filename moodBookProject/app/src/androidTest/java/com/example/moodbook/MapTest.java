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


import static org.junit.Assert.assertEquals;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;

public class MapTest {
    private Solo solo;

    @Rule
    public ActivityTestRule<LoginActivity> rule = new ActivityTestRule<>(LoginActivity.class,
            true, true);

    @Before
    public void setUp(){
        solo = new Solo(getInstrumentation(), rule.getActivity());
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

    public void login(){
        solo.enterText((EditText) solo.getView(R.id.email), "maptesting@gmail.com");
        solo.enterText((EditText) solo.getView(R.id.password), "password");
        solo.clickOnButton("login");
    }

    @Test
    public void testMap() throws UiObjectNotFoundException {
        // switch to mood map fragment
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
        solo.clickOnImageButton(0);
        solo.clickOnText("My Mood Map");
        solo.sleep(3000);

        MapView mapView = (MapView) solo.getView(R.id.mapView);

        // test if map is ready to be used
        assertEquals(  "Expected map view to be ready","MAP READY", mapView.getContentDescription());

        // test if map view is shown
        assertEquals("Expected mapView.shown() is true",true, mapView.isShown());

        // find marker on map view
        UiDevice device = UiDevice.getInstance(getInstrumentation());
        UiObject marker = device.findObject(new UiSelector()
                .descriptionContains("2019-11-29 08:48:51"));

        marker.click();

        // test emotion text
        TextView emotionText = (TextView) solo.getView(R.id.view_emotion);
        assertEquals(emotionText.getText().toString(), "sad");

        // test dateTime text
        TextView dateTimeText = (TextView) solo.getView(R.id.view_date_time);
        assertEquals(dateTimeText.getText().toString(), "2019-11-29 at 08:48:51");

    }
}
