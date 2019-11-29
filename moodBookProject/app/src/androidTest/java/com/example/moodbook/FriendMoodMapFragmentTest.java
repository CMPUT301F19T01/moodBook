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
     * Test if map is loaded and shown
     */
    @Test
    public void test() {
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
        assertEquals("Expected mapView.shown() is true",true, mapView.isShown());
    }

}
