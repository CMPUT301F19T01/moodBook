package com.example.moodbook;

import android.widget.EditText;
import android.widget.TextView;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObject2;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;


import com.example.moodbook.ui.login.LoginActivity;
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
        solo.enterText((EditText) solo.getView(R.id.email), "mapTest@gmail.com");
        solo.enterText((EditText) solo.getView(R.id.password), "password");
        solo.clickOnButton("login");
    }

    @Test
    public void test() throws UiObjectNotFoundException {
        // switch to mood map fragment
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
        solo.clickOnImageButton(0);
        solo.clickOnText("My Mood Map");
        solo.sleep(3000);

        // find marker on map view
        UiDevice device = UiDevice.getInstance(getInstrumentation());
        UiObject marker = device.findObject(new UiSelector()
                .descriptionContains("MAP READY")
                .childSelector(new UiSelector().instance(3)));

        // click marker to open up dialog
        marker.click();

        // test emotion text
        TextView emotionText = (TextView) solo.getView(R.id.view_emotion);
        assertEquals(emotionText.getText().toString(), "happy");

        // test dateTime text
        TextView dateTimeText = (TextView) solo.getView(R.id.view_date_time);
        assertEquals(dateTimeText.getText().toString(), "2019-11-28 at 18:58:57");

    }
}
