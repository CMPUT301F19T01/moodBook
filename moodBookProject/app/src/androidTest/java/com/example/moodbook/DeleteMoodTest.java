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
    @Test
    public void testDeleteMood() {
        // add a new mood
        TestHelper.addMoodBasic(solo);

        // delete the new mood, and check if the mood is deleted from db successfully
        TestHelper.deleteMood(solo);
        assertTrue(solo.waitForLogMessage("Deleted mood."));
    }
}
