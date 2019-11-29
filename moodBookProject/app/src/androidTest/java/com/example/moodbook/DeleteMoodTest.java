// Reference: https://stackoverflow.com/questions/24664730/writing-a-robotium-test-to-swipe-open-an-item-on-a-swipeable-listview

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
    public void setUp() {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
        TestHelper.setup(solo);
    }

    /**
     * Tests if Swipe-to-delete from Mood History works
     */
<<<<<<< HEAD
=======
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
>>>>>>> kathTest
    @Test
    public void testDeleteMood() {
        // add a new mood
        TestHelper.addMoodBasic(solo);

        // delete the new mood, and check if the mood is deleted from db successfully
        TestHelper.deleteMood(solo);
        assertTrue(solo.waitForLogMessage("Deleted mood."));
    }
}
