package com.example.moodbook;

import android.widget.EditText;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.moodbook.ui.login.LoginActivity;
import com.example.moodbook.ui.request.RequestFragment;
import com.robotium.solo.Solo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;


public class RequestFragmentTest {
    private Solo solo;

    @Rule
    public ActivityTestRule<MainActivity> rule =
            new ActivityTestRule<>(MainActivity.class, true, true);


    @Before
    public void setUp() throws Exception{
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
    }

    /**
     * Tests login with test@test.com and password testtest
     */
    @Test
    public void sendRequest(){
        solo.enterText((EditText) solo.getView(R.id.usernameEditText), "elisa");
        solo.clickOnButton("Send Request");
        solo.sleep(5000); // wait for activity to change
    }
}
