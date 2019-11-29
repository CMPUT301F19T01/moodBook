package com.example.moodbook;

import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.moodbook.ui.Request.RequestsAdapter;
import com.example.moodbook.ui.login.LoginActivity;
import com.robotium.solo.Solo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import static junit.framework.TestCase.assertTrue;

public class AcceptDeclineTest {
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
            for (int i = 0; i < 4; i++){
                solo.sendKey(Solo.DOWN);
            }
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
        solo.enterText((EditText) solo.getView(R.id.email), "test@test.com");
        solo.enterText((EditText) solo.getView(R.id.password), "testtest");
        solo.clickOnButton("login");
    }

    /**
     * Tests accept requests
     */
    @Test
    public void AcceptRequest(){
        solo.clickOnImageButton(0);
        solo.clickOnText("My Requests");
        solo.sleep(5000);
        solo.waitForText("Friend Requests");

        ListView requestListView = (ListView) solo.getView(R.id.request_listView);
        RequestsAdapter requestsAdapter = (RequestsAdapter)requestListView.getAdapter();

        if (requestsAdapter.getCount() >= 1){

            for(int i = 1; i<requestsAdapter.getCount();i++){
                TextView textView = (TextView) requestListView.getChildAt(i);
                solo.clickOnButton("ACCEPT");
                assertTrue(solo.waitForText("Accepted Request"));
            }
        }
        else {
            Log.e(AcceptDeclineTest.class.getSimpleName(), "No Requests");
        }
    }

    /**
     * Tests decline requests
     */
    @Test
    public void DeclineRequest(){
        solo.clickOnImageButton(0);
        solo.clickOnText("My Requests");
        solo.sleep(5000);
        solo.waitForText("Friend Requests");

        ListView requestListView = (ListView) solo.getView(R.id.request_listView);
        RequestsAdapter requestsAdapter = (RequestsAdapter)requestListView.getAdapter();

        if(requestsAdapter.getCount() >= 1){
            for(int i = 1; i<requestsAdapter.getCount();i++){
                TextView textView = (TextView) requestListView.getChildAt(i);
                solo.clickOnButton("DECLINE");
                assertTrue(solo.waitForText("Declined Request"));
            }
        }
        else {
            Log.e(AcceptDeclineTest.class.getSimpleName(), "No Requests");
        }
    }
}

