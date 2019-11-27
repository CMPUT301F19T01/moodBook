package com.example.moodbook;
import android.widget.EditText;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.moodbook.ui.home.EditMoodActivity;
import com.example.moodbook.ui.login.LoginActivity;
import com.robotium.solo.Solo;

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
    public void setUp() throws Exception{
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
        // logout if logged in
        if (solo.searchText("Mood History")){
            solo.clickOnImageButton(0);
            for (int i = 0; i < 4; i++){
                solo.sendKey(Solo.DOWN);
            }
            solo.clickOnText("Logout");
            solo.sleep(2000);
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
        solo.sleep(2000); // wait for activity to change
        solo.clickOnView(solo.getView(R.id.create_emotion_spinner));//emotion --Picks alone
        solo.clickOnView(solo.getView(R.id.create_situation_spinner));
        solo.pressSpinnerItem(1,1);
        solo.clickOnView(solo.getView(R.id.create_add_button));
    }

    @Test
    public void clickEdit(){
        addMood();
        solo.clickInRecyclerView(0);
        solo.sleep(2000); // wait for activity to change
        assertTrue(solo.waitForText("Created"));
    }

}
