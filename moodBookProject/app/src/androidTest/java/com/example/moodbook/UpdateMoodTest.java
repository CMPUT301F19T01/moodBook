package com.example.moodbook;

import android.widget.EditText;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.moodbook.ui.home.EditMoodActivity;
import com.example.moodbook.ui.login.LoginActivity;
import com.robotium.solo.Solo;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static junit.framework.TestCase.assertTrue;

public class UpdateMoodTest {
    private Solo solo;

    @Rule
    public ActivityTestRule<LoginActivity> rule =
            new ActivityTestRule<>(LoginActivity.class, true, true);


    @Before
    public void setUp() throws Exception{
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
        TestHelper.setup(solo);
    }


    @Test
    public void editMoodTest() {
        // wait for activity to change
        solo.sleep(5000);
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        // ensure current fragment is for Mood History
        Assert.assertTrue(solo.searchText("Mood History"));
        TestHelper.addMoodBasic(solo);

        solo.clickInRecyclerView(1);
        solo.waitForActivity(ViewMoodActivity.class, 2000); // wait for activity to change
        solo.clickOnButton(0);

        solo.waitForActivity(EditMoodActivity.class, 2000); // wait for activity to change
        solo.clickOnView(solo.getView(R.id.edit_emotion_spinner));//emotion --Picks alone
        solo.pressSpinnerItem(0,1);
        solo.clickOnView(solo.getView(R.id.edit_reason_editText));
        solo.clickOnView(solo.getView(R.id.edit_situation_spinner));
        solo.pressSpinnerItem(1,1);
        solo.clickOnView(solo.getView(R.id.edit_save_button)); //Select SAVE Button
        assertTrue(solo.waitForText("Updated"));
        TestHelper.deleteMood(solo);
    }
}
