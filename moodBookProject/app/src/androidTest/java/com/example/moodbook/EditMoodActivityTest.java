package com.example.moodbook;
import android.app.Activity;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import com.robotium.solo.Solo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * Test Class for EditMoodActivity. All UI tests are written here. Robotium test framework is used
 */
public class EditMoodActivityTest {
    private Solo solo;
    private Solo solo2;

    @Rule
    public ActivityTestRule<MainActivity> rule =
            new ActivityTestRule<>(MainActivity.class,true,true);

    @Rule
    public ActivityTestRule<EditMoodActivity> rule2 =
            new ActivityTestRule<>(EditMoodActivity.class,true,true);

    /**
     * Runs before all tests and creates solo instance.
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception{
        solo = new Solo(InstrumentationRegistry.getInstrumentation(),rule.getActivity());
        solo2 = new Solo(InstrumentationRegistry.getInstrumentation(),rule.getActivity());
    }

    @Test
    public void start() throws Exception{
        Activity activity = rule.getActivity();
    }
//    @Test
//    public void clickAdd(){
//        solo.clickOnView(solo.getView(R.id.mood_history_add_button));
//        solo.sleep(5000); // wait for activity to change
//
//    }
//
//    @Test
//    public void addMood(){
//        solo2.clickOnView(solo2.getView(R.id.create_date_button)); //date button
//        solo2.clickOnView(solo2.getView(R.id.create_time_button)); //time button
//        solo2.clickOnView(solo2.getView(R.id.create_location_button)); //time button
//
//        solo2.clickOnView(solo2.getView(R.id.create_emotion_spinner, 0));//emotion --Picks alone
//        solo2.clickOnView(solo2.getView(R.id.create_location_button));
//        solo2.clickOnView(solo2.getView(R.id.create_emotion_spinner, 0));
//
//        solo2.clickOnView(solo2.getView(R.id.create_add_button)); //Select CONFIRM Button
//
//    }
//

    @Test
    public void clickItem(){
        solo.clickOnView(solo.getView(R.id.mood_history_listView));//Can't get it to click item on list makes sense since initially there is nothing on list.
        solo.sleep(5000); // wait for activity to change

    }

    @Test
    public void editMood(){
        solo2.clickOnView(solo2.getView(R.id.edit_location_button)); //time button

        solo2.clickOnView(solo2.getView(R.id.edit_emotion_spinner, 0));//emotion --Picks alone
        solo2.clickOnView(solo2.getView(R.id.edit_location_button));//Clicks again
        solo2.clickOnView(solo2.getView(R.id.edit_emotion_spinner, 0));

        solo2.clickOnView(solo2.getView(R.id.edit_save_button)); //Select CONFIRM Button

    }


}
