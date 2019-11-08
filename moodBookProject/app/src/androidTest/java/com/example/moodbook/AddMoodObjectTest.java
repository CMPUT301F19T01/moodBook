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

public class AddMoodObjectTest {

    private Solo solo;
    private Solo solo2;


    @Rule
    public ActivityTestRule<LoginActivity> rule =
            new ActivityTestRule<>(LoginActivity.class, true, true);

//    @Rule
//    public  ActivityTestRule<CreateMoodActivity> rule2 =
//            new ActivityTestRule<>(CreateMoodActivity.class,true,true);


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


    /**
     * Tests that the Createmood UI works
     */
    @Test
    public void addMood(){
  //      solo2 = new Solo(InstrumentationRegistry.getInstrumentation(),rule2.getActivity());
        solo.clickOnView(solo.getView(R.id.mood_history_add_button));
        solo.sleep(5000); // wait for activity to change
        solo.clickOnView(solo.getView(R.id.create_date_button)); //date button
        solo.clickOnView(solo.getView(R.id.create_time_button)); //time button
        solo.clickOnView(solo.getView(R.id.create_location_button)); //location button
        solo.clickOnView(solo.getView(R.id.create_emotion_spinner));//emotion --Picks alone
        solo.pressSpinnerItem(0,1);
        solo.enterText((EditText) solo.getView(R.id.create_reason_editText), "test");
        solo.clickOnView(solo.getView(R.id.create_situation_spinner));
        solo.pressSpinnerItem(1,1);
        solo.clickOnView(solo.getView(R.id.create_add_button));
        assertTrue(solo.waitForLogMessage("Mood successfully added"));
    }
}





