package com.example.moodbook;
import android.widget.EditText;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.moodbook.ui.home.CreateMoodActivity;
import com.example.moodbook.ui.home.EditMoodActivity;
import com.example.moodbook.ui.login.LoginActivity;
import com.robotium.solo.Solo;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static junit.framework.TestCase.assertTrue;

/**
 * Test Class for EditMoodActivity. All UI tests are written here. Robotium test framework is used
 */
public class EditMoodActivityTest {
    private Solo solo;

    @Rule
    public ActivityTestRule<LoginActivity> rule =
            new ActivityTestRule<>(LoginActivity.class, true, true);

    @Before
    public void setUp() {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
        TestHelper.setup(solo);
    }

    @Test
    public void clickEdit(){
        // wait for activity to change
        solo.sleep(5000);
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        // ensure current fragment is for Mood History
        Assert.assertTrue(solo.searchText("Mood History"));

        // back to Mood History
        solo.waitForActivity(MainActivity.class, 5000); // wait for activity to change

        solo.clickInRecyclerView(0);
        solo.sleep(2000); // wait for activity to change
        solo.clickOnText("Edit");
        assertTrue(solo.waitForActivity(EditMoodActivity.class));
    }

}
