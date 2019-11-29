package com.example.moodbook;

import android.util.Log;
import android.widget.ListView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.moodbook.ui.friendMood.FriendMood;
import com.example.moodbook.ui.friendMood.FriendMoodListAdapter;
import com.example.moodbook.ui.login.LoginActivity;
import com.robotium.solo.Solo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class FriendMoodFragmentTest {
    private Solo solo;

    @Rule
    public ActivityTestRule<LoginActivity> rule =
            new ActivityTestRule<>(LoginActivity.class, true, true);


    @Before
    public void setUp() {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
        TestHelper.setup(solo);
    }

    /**
     * Check if friend moods are sorted starting from most recent
     */
    @Test
    public void testSorting() {
        // switch to Friend Moodbook
        solo.clickOnImageButton(0);
        solo.clickOnText("Friend MoodBook");
        solo.sleep(5000);
        solo.waitForText("Friend Moods");

        ListView friendMoodListView = (ListView) solo.getView(R.id.friend_mood_listView);
        FriendMoodListAdapter friendMoodListAdapter = (FriendMoodListAdapter)friendMoodListView.getAdapter();
        // test if friendMoods in ListView are sorted
        if(friendMoodListAdapter.getCount() >= 2) {
            for (int i = 1; i < friendMoodListAdapter.getCount(); i++) {
                FriendMood a = (FriendMood)friendMoodListAdapter.getItem(i-1);
                FriendMood b = (FriendMood)friendMoodListAdapter.getItem(i);
                assertTrue(a.getMood().getDateTime().compareTo(b.getMood().getDateTime()) > 0);
            }
        }
        else {
            Log.e(FriendMoodFragmentTest.class.getSimpleName(), "Need at least 2 friends with recent mood");
        }
    }


}
