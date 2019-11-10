package com.example.moodbook;

import android.widget.Button;
import android.widget.EditText;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.moodbook.ui.login.LoginActivity;
import com.robotium.solo.Solo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Test class for HomeFragment. All the UI tests are written here.
 * Robotium test framework is used
 */
public class HomeFragmentTest {
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

    /**
     * Check if moods are sorted starting from most recent
     */
    @Test
    public void checkSorting(){
        solo.sleep(5000); // wait for activity to change
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        // ensure current fragment is for Mood History
        assertTrue(solo.searchText("Mood History"));

        final RecyclerView moodListView = (RecyclerView) solo.getView(R.id.mood_history_listView);
        MoodListAdapter moodAdapter = (MoodListAdapter)moodListView.getAdapter();
        // check if moods in existing RecyclerView are sorted
        for (int i = 1; i < moodAdapter.getItemCount(); i++) {
            Mood a = moodAdapter.getItem(i-1);
            Mood b = moodAdapter.getItem(i);
            assertTrue(a.getDateTime().compareTo(b.getDateTime()) > 0);
        }

        // add a new mood, and check if the new mood is the top item in RecyclerView
        solo.clickOnView(solo.getView(R.id.mood_history_add_button));
        solo.sleep(5000); // wait for activity to change
        assertTrue(solo.waitForActivity(CreateMoodActivity.class));
        solo.clickOnView(solo.getView(R.id.create_date_button));
        solo.clickOnView(solo.getView(R.id.create_time_button));
        solo.pressSpinnerItem(0, 1);
        String new_mood_date = ((Button)solo.getView(R.id.create_date_button)).getText().toString();
        String new_mood_time = ((Button)solo.getView(R.id.create_time_button)).getText().toString();
        solo.clickOnButton("ADD");

        // back to Mood History
        solo.sleep(5000); // wait for activity to change
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        // ensure current fragment is for Mood History
        assertTrue(solo.searchText("Mood History"));
        Mood new_mood = moodAdapter.getItem(0);
        assertEquals(new_mood.getDateText(), new_mood_date);
        assertEquals(new_mood.getTimeText(), new_mood_time);
    }

    /**
     * Check if filter filters out non-matching mood
     * TODO: need to find menu
     */
    @Test
    public void checkFiltering() {
        solo.sleep(5000); // wait for activity to change
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        MainActivity activity = (MainActivity) solo.getCurrentActivity() ;
        // ensure current fragment is for Mood History
        assertTrue(solo.searchText("Mood History"));

        solo.clickOnView(solo.getView(R.id.mood_history_action_search));

        /*MenuItem searchItem = (MenuItem)solo.getView(R.id.mood_history_action_search);
        //MenuItem searchItem = (MenuItem)solo.getView(R.id.mood_history_action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQuery("happy",false);*/
    }
}
