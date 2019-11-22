package com.example.moodbook;

import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;

import androidx.appcompat.view.menu.ActionMenuItemView;
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
    private Solo solo2;

    @Rule
    public ActivityTestRule<LoginActivity> rule =
            new ActivityTestRule<>(LoginActivity.class, true, true);


    @Before
    public void setUp() throws Exception{
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
        solo2 = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
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
        // wait for activity to change
        solo.sleep(5000);
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        // ensure current fragment is for Mood History
        assertTrue(solo.searchText("Mood History"));

        final RecyclerView moodListView = (RecyclerView) solo.getView(R.id.mood_history_listView);
        MoodListAdapter moodAdapter = (MoodListAdapter)moodListView.getAdapter();
        // check if moods in RecyclerView are sorted
        for (int i = 1; i < moodAdapter.getItemCount(); i++) {
            Mood a = moodAdapter.getItem(i-1);
            Mood b = moodAdapter.getItem(i);
            assertTrue(a.getDateTime().compareTo(b.getDateTime()) > 0);
        }

        // add a new mood, and check if the new mood is the top item in RecyclerView
        solo.clickOnView(solo.getView(R.id.mood_history_add_button));
        solo.waitForActivity(CreateMoodActivity.class, 5000); // wait for activity to change
        solo.clickOnView(solo.getView(R.id.create_date_button));
        solo.clickOnView(solo.getView(R.id.create_time_button));
        solo.pressSpinnerItem(0, 1);
        String new_mood_date = ((Button)solo.getView(R.id.create_date_button)).getText().toString();
        String new_mood_time = ((Button)solo.getView(R.id.create_time_button)).getText().toString();
        solo.clickOnButton("ADD");

        // back to Mood History
        solo.waitForActivity(MainActivity.class, 5000); // wait for activity to change
        // ensure current fragment is for Mood History
        assertTrue(solo.searchText("Mood History"));
        Mood new_mood = moodAdapter.getItem(0);
        assertEquals(new_mood_date, new_mood.getDateText());
        assertEquals(new_mood_time, new_mood.getTimeText());
    }

    /**
     * Check if filter filters out mood with non-matching emotion
     */
    @Test
    public void checkFiltering() {
        // wait for activity to change
        solo2.sleep(5000);
        solo2.assertCurrentActivity("Wrong Activity", MainActivity.class);
        // ensure current fragment is for Mood History
        assertTrue(solo2.searchText("Mood History"));

        final RecyclerView moodListView = (RecyclerView) solo2.getView(R.id.mood_history_listView);
        MoodListAdapter moodAdapter = (MoodListAdapter)moodListView.getAdapter();

        // click search menu option
        View menu_search = solo2.getView(R.id.mood_history_action_search);
        solo2.clickOnView(menu_search);

        // type "happy" into searchView
        MenuItem menu_searchItem = ((ActionMenuItemView)menu_search).getItemData();
        SearchView searchView = (SearchView) menu_searchItem.getActionView();
        searchView.performClick();
        solo2.enterText(0,"happy");
        solo2.sleep(3000);

        // filter first to get updated moodAdapter
        moodAdapter.getFilter().filter("happy");
        solo2.sleep(3000);
        // check if moods in RecyclerView are "happy" only
        for (int i = 0; i < moodAdapter.getItemCount(); i++) {
            Mood a = moodAdapter.getItem(i);
            assertEquals("happy", a.getEmotionText());
        }
    }
}
