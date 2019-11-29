package com.example.moodbook;

import android.widget.EditText;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.moodbook.ui.login.LoginActivity;
import com.robotium.solo.Solo;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static junit.framework.TestCase.assertTrue;

public class AddMoodTest {

    private Solo solo;

    @Rule
    public ActivityTestRule<LoginActivity> rule =
            new ActivityTestRule<>(LoginActivity.class, true, true);

    @Before
    public void setUp() throws Exception{
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
        TestHelper.setup(solo);
    }

    /**
     * Tests if CreateMood UI works
     */
    @Test
    public void testeAddMood(){
        // wait for activity to change
        solo.sleep(5000);
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        // ensure current fragment is for Mood History
        Assert.assertTrue(solo.searchText("Mood History"));

        // add a new mood, and check if the new mood is added into db successfully
        TestHelper.addMoodComplete(solo);
        assertTrue(solo.waitForLogMessage("Mood successfully added"));

        // delete the new mood
        TestHelper.deleteMood(solo);
    }
}





