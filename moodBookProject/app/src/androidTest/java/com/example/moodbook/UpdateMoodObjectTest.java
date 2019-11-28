package com.example.moodbook;

import android.widget.EditText;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.moodbook.ui.login.LoginActivity;
import com.robotium.solo.Solo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static junit.framework.TestCase.assertTrue;

public class UpdateMoodObjectTest {
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


    @Test
    public void editMoodTest() {
        solo.clickInRecyclerView(1);
        solo.sleep(2000); // wait for activity to change
        solo.clickOnText("Edit");
        solo.clickOnView(solo.getView(R.id.edit_emotion_spinner));//emotion --Picks alone
        solo.pressSpinnerItem(0,1);
        solo.clickOnView(solo.getView(R.id.edit_reason_editText));
        solo.clickOnView(solo.getView(R.id.edit_situation_spinner));
        solo.pressSpinnerItem(1,1);
        solo.clickOnView(solo.getView(R.id.edit_save_button)); //Select SAVE Button
        assertTrue(solo.waitForText("Updated"));
    }
}
