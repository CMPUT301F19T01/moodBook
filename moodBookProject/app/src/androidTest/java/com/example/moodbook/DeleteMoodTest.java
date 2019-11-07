package com.example.moodbook;

import android.graphics.PointF;
import android.widget.EditText;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.moodbook.ui.login.LoginActivity;
import com.robotium.solo.Solo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

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
// NOT WORKING
//    @Test
//    public void testDelete(){
//        solo.swipe(new PointF(400, 200), new PointF(400, 200), new PointF(10, 200), new PointF(10, 200));
//        assertTrue(solo.waitForLogMessage("Deleted mood."));
//    }
}
