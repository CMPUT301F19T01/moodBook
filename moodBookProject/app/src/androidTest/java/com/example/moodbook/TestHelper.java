package com.example.moodbook;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.moodbook.ui.home.CreateMoodActivity;
import com.robotium.solo.Solo;

import java.util.HashMap;

import static com.google.firebase.firestore.util.Assert.fail;

public class TestHelper {

    /**
     * This is used to setup test
     * @param solo
     */
    public static void setup(Solo solo) {
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
            TestHelper.login(solo);
        }
    }

    /**
     * This is used in tests to first login to the app
     */
    public static void login(Solo solo) {
        solo.enterText((EditText) solo.getView(R.id.email), "test@test.com");
        solo.enterText((EditText) solo.getView(R.id.password), "testtest");
        solo.clickOnButton("login");
    }

    /**
     * This is needed to scroll up sidebar in order to find Logout button on smaller device
     */
    private static void scrollToLogout(Solo solo) {
        int fromX, toX, fromY, toY;
        int[] location = new int[2];

        View row = solo.getText("User");
        row.getLocationInWindow(location);
        // fail if the view with text cannot be located in the window
        if (location.length == 0) {
            fail("Could not find text: User");
        }
        fromX = location[0];
        fromY = location[1];
        toX = location[0];
        toY = fromY-200;
        solo.drag(fromX, toX, fromY, toY, 20);
        solo.clickOnText("Logout");
    }

    /**
     * This add a mood with only mandatory fields for testing
     * @param solo
     * @return  data about mood fields
     */
    public static HashMap<String,String> addMoodBasic(Solo solo) {
        HashMap<String, String> newMoodData = new HashMap<>();

        solo.clickOnView(solo.getView(R.id.mood_history_add_button));
        solo.waitForActivity(CreateMoodActivity.class, 5000);   // wait for activity to change
        solo.clickOnView(solo.getView(R.id.create_emotion_spinner));    // pick emotion: happy
        solo.pressSpinnerItem(0, 1);

        // set data
        newMoodData.put("date", ((Button)solo.getView(R.id.create_date_button)).getText().toString() );
        newMoodData.put("time", ((Button)solo.getView(R.id.create_time_button)).getText().toString() );
        newMoodData.put("emotion", "happy");

        solo.clickOnButton("ADD");
        // back to Mood History
        solo.waitForActivity(MainActivity.class, 5000); // wait for activity to change

        return newMoodData;
    }

    /**
     * This add a mood with all the fields for testing
     * @param solo
     * @return  data about mood fields
     */
    public static HashMap<String,String> addMoodComplete(Solo solo) {
        HashMap<String, String> newMoodData = new HashMap<>();

        solo.clickOnView(solo.getView(R.id.mood_history_add_button));
        solo.waitForActivity(CreateMoodActivity.class, 5000);   // wait for activity to change
        solo.clickOnView(solo.getView(R.id.create_emotion_spinner));    // pick emotion: happy
        solo.pressSpinnerItem(0, 1);
        solo.clickOnView(solo.getView(R.id.create_location_button));    // pick location
        solo.waitForText("Confirm", 1, 3000);
        solo.clickOnText("Confirm");
        solo.clickOnView(solo.getView(R.id.create_situation_spinner));  // pick situation: alone
        solo.pressSpinnerItem(1,1);
        solo.enterText((EditText) solo.getView(R.id.create_reason_editText), "test");   // reason_text

        // set data
        newMoodData.put("date", ((Button)solo.getView(R.id.create_date_button)).getText().toString() );
        newMoodData.put("time", ((Button)solo.getView(R.id.create_time_button)).getText().toString() );
        newMoodData.put("emotion", "happy");
        newMoodData.put("location", ((Button)solo.getView(R.id.create_location_button)).getText().toString() );
        newMoodData.put("reason_text", ((EditText)solo.getView(R.id.create_reason_editText)).getText().toString() );

        solo.clickOnButton("ADD");
        // back to Mood History
        solo.waitForActivity(MainActivity.class, 5000); // wait for activity to change

        return newMoodData;
    }

    /**
     * This is used to delete a mood after just adding it for testing
     * @param solo
     */
    public static void deleteMood(Solo solo) {
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
    }
}
