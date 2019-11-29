package com.example.moodbook;

import android.content.Context;
import android.graphics.PointF;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.matcher.BoundedMatcher;
import androidx.test.internal.util.Checks;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.moodbook.ui.home.MoodListAdapter;
import com.example.moodbook.ui.login.LoginActivity;
import com.robotium.solo.Solo;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static com.google.firebase.firestore.util.Assert.fail;
import static junit.framework.TestCase.assertTrue;

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
        //TestHelper.setup(solo);
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
    public void testMoodHistoryEmoticonColor() {
        // wait for activity to change
        solo.sleep(5000);
        //solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        // ensure current fragment is for Mood History
        Assert.assertTrue(solo.searchText("Mood History"));

        final RecyclerView moodListView = (RecyclerView) solo.getView(R.id.mood_history_listView);

        View rowView = moodListView.getLayoutManager().findViewByPosition(0);
        if(rowView != null) {
            // get corresponding color from emotion text
            String rowEmotion = (String)((TextView)rowView.findViewById(R.id.mood_item_emotion_text)).getText();
            int rowEmotionColorID = Mood.Emotion.getColorResourceId(rowEmotion);
            int rowEmotionColor = ContextCompat.getColor(context, rowEmotionColorID);

            // get actual color from view background
            ColorDrawable rowViewBackground = (ColorDrawable)rowView.findViewById(R.id.mood_item_foreground).getBackground();
            int rowViewColor = rowViewBackground.getColor();
            // Test if emotion color is consistent with view background color
            Assert.assertEquals(rowViewColor, rowEmotionColor);
        }
    }

}
