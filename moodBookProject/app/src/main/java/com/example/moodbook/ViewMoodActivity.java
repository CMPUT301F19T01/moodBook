package com.example.moodbook;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;

/**
 * This activity is used to  enable a user to be to view detials for a specific mood
 *
 */
public class ViewMoodActivity extends Activity {

    Button closeButton;
    private static Mood mMood;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_mood);
        closeButton = findViewById(R.id.close_button);

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;

        getWindow().setLayout((int)(width * 0.8), (int)(height * 0.8));

        Log.i("as;ldf", mMood.getEmotionText());

    }

    public static void setMood(Mood mood){
        mMood = mood;
    }

}
