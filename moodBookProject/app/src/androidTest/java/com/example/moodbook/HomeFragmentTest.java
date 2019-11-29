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

import com.example.moodbook.ui.home.CreateMoodActivity;
import com.example.moodbook.ui.home.MoodListAdapter;
import com.example.moodbook.ui.login.LoginActivity;
import com.robotium.solo.Solo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.HashMap;

import static com.google.firebase.firestore.util.Assert.fail;
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
        TestHelper.setup(solo);
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
        // test if moods in RecyclerView are sorted
        for (int i = 1; i < moodAdapter.getItemCount(); i++) {
            Mood a = moodAdapter.getItem(i-1);
            Mood b = moodAdapter.getItem(i);
            assertTrue(a.getDateTime().compareTo(b.getDateTime()) > 0);
        }

        // add a new mood, and check if the new mood is the top item in RecyclerView
        HashMap<String,String> moodData = TestHelper.addMoodBasic(solo);

        // back to Mood History
        solo.waitForActivity(MainActivity.class, 5000); // wait for activity to change
        // ensure current fragment is for Mood History
        assertTrue(solo.searchText("Mood History"));
        // test if the first mood is the one just added
        Mood new_mood = moodAdapter.getItem(0);
        assertEquals(moodData.get("date"), new_mood.getDateText());
        assertEquals(moodData.get("time"), new_mood.getTimeText());
        assertEquals(moodData.get("emotion"), new_mood.getEmotionText());

        // delete the new mood
        TestHelper.deleteMood(solo);
    }

    /**
     * Check if filter filters out mood with non-matching emotion
     */
    //@Test
    public void checkFiltering() {
        // wait for activity to change
        solo.sleep(5000);
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        // ensure current fragment is for Mood History
        assertTrue(solo.searchText("Mood History"));

        final RecyclerView moodListView = (RecyclerView) solo.getView(R.id.mood_history_listView);
        MoodListAdapter moodAdapter = (MoodListAdapter)moodListView.getAdapter();

        // click search menu option
        View menu_search = solo.getView(R.id.mood_history_action_search);
        solo.clickOnView(menu_search);

        // type "happy" into searchView
        MenuItem menu_searchItem = ((ActionMenuItemView)menu_search).getItemData();
        SearchView searchView = (SearchView) menu_searchItem.getActionView();
        searchView.performClick();
        solo.enterText(0,"happy");
        solo.sleep(3000);

        // filter first to get updated moodAdapter
        moodAdapter.getFilter().filter("happy");
        solo.sleep(3000);
        // check if moods in RecyclerView are "happy" only
        for (int i = 0; i < moodAdapter.getItemCount(); i++) {
            Mood a = moodAdapter.getItem(i);
            assertEquals("happy", a.getEmotionText());
        }
    }
}
