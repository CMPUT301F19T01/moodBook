package com.example.moodbook;

import android.widget.EditText;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.moodbook.ui.login.LoginActivity;
import com.google.android.gms.maps.MapView;
import com.robotium.solo.Solo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class MyMoodMapFragmentTest {
    private Solo solo;

    @Rule
    public ActivityTestRule<LoginActivity> rule = new ActivityTestRule<>(LoginActivity.class,
            true, true);

    @Before
    public void setUp(){
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
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
        solo.enterText((EditText) solo.getView(R.id.email), "mapTest@gmail.com");
        solo.enterText((EditText) solo.getView(R.id.password), "password");
        solo.clickOnButton("login");
    }

    /**
     * Test if map is loaded and shown
     */
    @Test
    public void testOpenMap(){
        // switch to mood map fragment
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
        solo.clickOnImageButton(0);
        solo.clickOnText("My Mood Map");
        solo.sleep(3000);

        // find map
        MapView mapView = (MapView) solo.getView(R.id.mapView);

        // test if map is ready to be used
        assertEquals(  "Expected map view to be ready","MAP READY", mapView.getContentDescription());

        // test if map view is shown
        assertTrue("Expected mapView.shown() is true", mapView.isShown());
    }

    /**
     *
     */
    @Test
    public void testViewMood(){
        // switch to mood map fragment
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
        solo.clickOnImageButton(0);
        solo.clickOnText("My Mood Map");
        solo.sleep(3000);

    }
}
