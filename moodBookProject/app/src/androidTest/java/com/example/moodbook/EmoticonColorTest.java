package com.example.moodbook;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.moodbook.ui.login.LoginActivity;
import com.robotium.solo.Solo;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;


public class EmoticonColorTest {
    private Solo solo;
    private Context context;

    @Rule
    public ActivityTestRule<LoginActivity> rule =
            new ActivityTestRule<>(LoginActivity.class, true, true);


    @Before
    public void setUp() {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
        context = InstrumentationRegistry.getInstrumentation().getTargetContext().getApplicationContext();
        TestHelper.setup(solo);
    }

//
//    @Test
//    public void testEmoticonColor(){
//        View row = solo.getText("happy");
//        Drawable rowBg = row.getBackground();
//        ColorDrawable b_color = (ColorDrawable) row.getBackground();
//
//
//    }

    @Test
    public void testEmoticonColor() {
        // wait for activity to change
        solo.sleep(5000);
        //solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        // ensure current fragment is for Mood History
        Assert.assertTrue(solo.searchText("Mood History"));

        final RecyclerView moodListView = (RecyclerView) solo.getView(R.id.mood_history_listView);

        // add a new mood if there is none
        if(moodListView.getAdapter().getItemCount() == 0) {
            TestHelper.addMoodBasic(solo);
            // back to Mood History
            solo.sleep(5000);       // wait for activity to change
            solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
            // ensure current fragment is for Mood History
            Assert.assertTrue(solo.searchText("Mood History"));
        }

        View rowView = moodListView.getLayoutManager().findViewByPosition(0);

        // get corresponding color from emotion text
        String rowEmotion = (String)((TextView)rowView.findViewById(R.id.mood_item_emotion_text)).getText();
        int rowEmotionColorID = Mood.Emotion.getColorResourceId(rowEmotion);
        int rowEmotionColor = ContextCompat.getColor(context, rowEmotionColorID);

        // get actual color from row view background
        ColorDrawable rowViewBackground = (ColorDrawable)rowView.findViewById(R.id.mood_item_foreground).getBackground();
        int rowViewColor = rowViewBackground.getColor();
        // Test if emotion color is consistent with row view background color
        Assert.assertEquals(rowViewColor, rowEmotionColor);

        // go to View Mood page
        solo.clickInRecyclerView(0);
        solo.waitForActivity(ViewMoodActivity.class, 2000); // wait for activity to change

        // get actual color from View Mood page background
        View viewMoodScrollView = solo.getView(R.id.viewPage);
        ColorDrawable scrollViewBackground = (ColorDrawable)viewMoodScrollView.getBackground();
        int scrollViewColor = rowViewBackground.getColor();
        // Test if emotion color is consistent with View Mood page background color
        Assert.assertEquals(rowViewColor, scrollViewColor);
    }

}
