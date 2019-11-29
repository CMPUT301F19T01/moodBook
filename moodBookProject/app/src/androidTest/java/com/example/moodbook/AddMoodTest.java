package com.example.moodbook;

import android.icu.text.SimpleDateFormat;
import android.view.View;
import android.widget.TextView;

import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

import com.example.moodbook.ui.login.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import java.util.Date;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(AndroidJUnit4.class)
@LargeTest
public class AddMoodTest {

    FirebaseFirestore db;
    FirebaseAuth mAuth;
    FirebaseUser user;

    private String validLoginEmail = "loise4@testing.com";
    private String validUsername = "loise4";
    private String validLoginPassword = "testing";
    private SimpleDateFormat formatter = new SimpleDateFormat("MMMM dd, yyyy hh:mm a");
    private static Date date1;
    private static Date date2;

    @Rule
    public ActivityTestRule<LoginActivity> activityRule =
            new ActivityTestRule<>(LoginActivity.class);

    // signs out first if ever app is initially logged in from other user
    @Before
    public void initialSignOut(){
        FirebaseAuth.getInstance().signOut();
    }

    @Before
    public void initialize_db(){
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
    }

    private void LogIntoActivity() throws InterruptedException {
        onView(withId(R.id.email))
                .perform(typeText(validLoginEmail), closeSoftKeyboard());
        onView(withId(R.id.password))
                .perform(typeText(validLoginPassword), closeSoftKeyboard());
        onView(withId(R.id.login)).perform(click());

        Thread.sleep(5000);

        // Check we logged in and we are on home fragment
        onView(withId(R.id.mood_history_add_button))
                .check(matches(isDisplayed()));
    }

    public static ViewAction setTextInTextView(final String value){
        return new ViewAction() {
            @SuppressWarnings("unchecked")
            @Override
            public Matcher<View> getConstraints() {
                return allOf(isDisplayed(), isAssignableFrom(TextView.class));
//                                            ^^^^^^^^^^^^^^^^^^^
// To check that the found view is TextView or it's subclass like EditText
// so it will work for TextView and it's descendants
            }

            @Override
            public void perform(UiController uiController, View view) {
                ((TextView) view).setText(value);
            }

            @Override
            public String getDescription() {
                return "replace text";
            }
        };
    }


    /**
     * Checks if home contains a mood
     * @throws InterruptedException
     */
    @Test
    public void MoodPresentHomeTest() throws InterruptedException {

        LogIntoActivity();

        // Check if home has a mood
        onView(withId(R.id.mood_item_emotion_image));
    }

    /**
     * Check if text of state is consistent with color and emoticon
     * @throws InterruptedException
     */

    @Test
    public void ViewMood() throws InterruptedException {

        LogIntoActivity();

        onView(withId(R.id.mood_history_listView))
                .check(matches(hasDescendant(withText("happy"))))
                .perform(click());

        Thread.sleep(3000);

        //checks if we're in happy view mood
        onView(withId(R.id.viewPage))
                .check(matches(isDisplayed()));
    }

    @Test
    public void AddMood() throws InterruptedException {
        LogIntoActivity();

        onView(withId(R.id.mood_history_add_button)).perform(click());

        Thread.sleep(3000);

        // Check we are in add/edit activity
        onView(withId(R.id.create_date_button))
                .check(matches(isDisplayed()));

        Thread.sleep(2000);

        // Add a mood
        onView(withId(R.id.create_emotion_spinner)).perform(click());

        Thread.sleep(2000);

        onData(allOf(is(instanceOf(String.class)))).atPosition(1).perform(click());

        Thread.sleep(2000);

        onView(withId(R.id.create_location_button)).perform(click());

        Thread.sleep(3000);

        try {
            //if permission needed
            onView(withText("Allow")).check(matches(isDisplayed())).perform(click());
        } catch (NoMatchingViewException e) {
            //view not displayed logic
        }

        Thread.sleep(10000);

        onView(withId(R.id.confirmButton)).perform(click());

        Thread.sleep(2000);

        onView(withId(R.id.create_situation_spinner)).perform(click());

        Thread.sleep(2000);

        onData(allOf(is(instanceOf(String.class)))).atPosition(1).perform(click());

        Thread.sleep(2000);

        onView(withId(R.id.create_reason_editText))
                .perform(setTextInTextView("my reason"));

        Thread.sleep(2000);

        onView(withId(R.id.create_add_button)).perform(click());

        Thread.sleep(3000);

        onView(withId(R.id.mood_history_listView))
                .check(matches(hasDescendant(withText("happy"))));

    }

    @After
    public void Exit(){
        FirebaseAuth.getInstance().signOut();
    }

}
