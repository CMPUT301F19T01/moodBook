package com.example.moodbook;

import android.util.Log;
import android.widget.EditText;

import androidx.fragment.app.Fragment;
import androidx.test.internal.runner.listener.InstrumentationRunListener;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.moodbook.ui.login.LoginActivity;
import com.google.android.gms.maps.GoogleMap;
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

    /**
     * Test database
     */
    @Test
    public void updateListTest(){
        // navigate to request sending

        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
        solo.clickOnImageButton(0);
        solo.clickOnText("My Mood Map");

    }

    /**
     * Test for drawing marker
     */
    @Test
    public void testMarker(){

    }

    @Test
    public void testNoLocationData(){

    }

    @Test
    public void deleteLocation(){

    }

    @Test
    public void changedLocation(){

    }


}
