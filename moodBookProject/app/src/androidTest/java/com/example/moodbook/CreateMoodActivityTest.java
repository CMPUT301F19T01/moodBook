package com.example.moodbook;

import android.app.Activity;
import android.widget.EditText;

import com.robotium.solo.Solo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

/**
 * Test class for CreateMoodActivity. All the UI tests are written here. Robotium test framework is
 used
 */
public class CreateMoodActivityTest {

    private Solo solo;
    private Solo solo2;


    @Rule
    public  ActivityTestRule<MainActivity> rule =
            new ActivityTestRule<>(MainActivity.class,true,true);

    @Rule
    public  ActivityTestRule<CreateMoodActivity> rule2 =
            new ActivityTestRule<>(CreateMoodActivity.class,true,true);


    /**
     * Runs before all tests and creates solo instance.
     * @throws Exception
     */
    @Before
    public void setUp()throws Exception{
        solo = new Solo(InstrumentationRegistry.getInstrumentation(),rule.getActivity());
        solo2 = new Solo(InstrumentationRegistry.getInstrumentation(),rule2.getActivity());
    }

    /**
     * Gets the Activity
     * @throws Exception
     */
    @Test
    public void start() throws Exception {
        Activity activity = rule.getActivity();
    }

    /**
     * Clicks on the Fab button for adding moods to go to createMood Activity
     */
    @Test
    public void clickAdd(){
        solo.clickOnView(solo.getView(R.id.mood_history_add_button));
        solo.sleep(5000); // wait for activity to change

    }

    @Test
    public void addMood(){
        solo.clickOnView(solo.getView(R.id.create_date_button)); //date button
        solo.clickOnView(solo.getView(R.id.create_time_button)); //time button
        solo.clickOnView(solo.getView(R.id.create_location_button)); //time button
        solo.clickOnView(solo.getView(R.id.create_emotion_spinner, 0));//emotion --Picks alone
        solo.clickOnView(solo.getView(R.id.create_location_button));
        solo.clickOnView(solo.getView(R.id.create_emotion_spinner, 0));
        solo.clickOnView(solo.getView(R.id.create_add_button)); //Select CONFIRM Button

    }






}
