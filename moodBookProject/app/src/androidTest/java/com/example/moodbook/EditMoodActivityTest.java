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
        solo.enterText((EditText) solo.getView(R.id.email), "kathleen@gmail.com");
        solo.enterText((EditText) solo.getView(R.id.password), "testing");
        solo.clickOnButton("login");
    }

    @Test
    public void clickEdit(){
        solo.clickInRecyclerView(0);
        solo.sleep(5000); // wait for activity to change
        assertTrue(solo.waitForActivity(EditMoodActivity.class));
    }

}
