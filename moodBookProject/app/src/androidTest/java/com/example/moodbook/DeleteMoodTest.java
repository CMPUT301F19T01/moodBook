package com.example.moodbook;

import android.graphics.PointF;
import android.view.View;
import android.widget.EditText;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.moodbook.ui.login.LoginActivity;
import com.robotium.solo.Solo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static com.google.firebase.firestore.util.Assert.fail;
import static junit.framework.TestCase.assertTrue;

public class DeleteMoodTest {
    private Solo solo;

    @Rule
    public ActivityTestRule<LoginActivity> rule =
            new ActivityTestRule<>(LoginActivity.class, true, true);


    @Before
    public void setUp() throws Exception{
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

    /**
     * used in tests to first login to the app
     */
    public void login(){
        solo.enterText((EditText) solo.getView(R.id.email), "test@test.com");
        solo.enterText((EditText) solo.getView(R.id.password), "testtest");
        solo.clickOnButton("login");
    }

    /**
     * Adds a mood for testing
     */
    public void addMood(){
        solo.clickOnView(solo.getView(R.id.mood_history_add_button));
        solo.sleep(5000); // wait for activity to change
        solo.clickOnView(solo.getView(R.id.create_date_button)); //date button
        solo.clickOnView(solo.getView(R.id.create_time_button)); //time button
        solo.clickOnView(solo.getView(R.id.create_location_button)); //location button
        solo.sleep(3000);
        solo.clickOnText("Confirm");
        solo.clickOnView(solo.getView(R.id.create_emotion_spinner));//emotion --Picks alone
        solo.pressSpinnerItem(0,1);
        solo.enterText((EditText) solo.getView(R.id.create_reason_editText), "test");
        solo.clickOnView(solo.getView(R.id.create_situation_spinner));
        solo.pressSpinnerItem(1,1);
        solo.clickOnView(solo.getView(R.id.create_add_button));
    }

    /**
     * Checks to see if mood got deleted after swiping
     * Reference: https://stackoverflow.com/questions/24664730/writing-a-robotium-test-to-swipe-open-an-item-on-a-swipeable-listview
     */
    @Test
    public void testDelete(){
        addMood();
        int fromX, toX, fromY, toY;
        int[] location = new int[2];

        View row = solo.getText("happy");
        row.getLocationInWindow(location);
        // fail if the view with text cannot be located in the window
        if (location.length == 0) {
            fail("Could not find text: " + "happy");
        }
        fromX = location[0] + 100;
        fromY = location[1];
        toX = location[0];
        toY = fromY;
        solo.drag(fromX, toX, fromY, toY, 10);
        solo.clickOnText("Yes");
        assertTrue(solo.waitForLogMessage("Deleted mood."));
    }
}
