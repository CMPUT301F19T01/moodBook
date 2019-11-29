package com.example.moodbook;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.moodbook.ui.home.EditMoodActivity;
import com.example.moodbook.ui.home.HomeFragment;
import com.google.firebase.auth.FirebaseAuth;

/**
 * This Activity is used to  enable a user to be to view details for a specific mood
 */
public class ViewMoodActivity extends AppCompatActivity {
    private TextView view_friend_name, view_date_time, view_emotion,
            view_reason, view_situation, view_location;
    private ImageView view_emoji, view_uploaded_pic;
    private Button view_edit_button, view_cancel_button;
    private DBMoodSetter moodDB;
    private FirebaseAuth mAuth;
    private ScrollView view_scrollView;
    private static final String TAG = ViewMoodActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_mood);
        mAuth = FirebaseAuth.getInstance();
        moodDB = new DBMoodSetter(mAuth, getApplicationContext(), TAG);
        view_friend_name = findViewById(R.id.view_friend_name);
        view_date_time = findViewById(R.id.view_date_time);
        view_reason = findViewById(R.id.view_reason);
        view_situation = findViewById(R.id.view_situation);
        view_location = findViewById(R.id.view_location);
        view_emotion = findViewById(R.id.view_emotion);
        view_emoji = findViewById(R.id.view_emoji);
        view_edit_button = findViewById(R.id.view_edit_button);
        view_cancel_button = findViewById(R.id.view_cancel_button);
        view_uploaded_pic = findViewById(R.id.view_uploaded_pic);
        view_scrollView = findViewById(R.id.viewPage);


        // Getting data from Intents
        final String intent_moodID = getIntent().getStringExtra("moodID");

        final String intent_date = getIntent().getStringExtra( "date");
        final String intent_time = getIntent().getStringExtra("time");
        view_date_time.setText("Created: " + intent_date +" at " + intent_time );

        final String intent_reason = getIntent().getStringExtra("reason_text");
        view_reason.setText((intent_reason==null)?"N/A":intent_reason);

        final String intent_lat = getIntent().getStringExtra("location_lat");
        final String intent_lon = getIntent().getStringExtra("location_lon");
        final String intent_address = getIntent().getStringExtra("location_address");
        view_location.setText((intent_address==null)?"N/A":intent_address);

        final String intent_situation =getIntent().getStringExtra("situation");
        view_situation.setText((intent_situation==null)?"N/A":intent_situation);

        final String intent_emotion =getIntent().getStringExtra("emotion");
        view_emotion.setText(intent_emotion);

        //Set emoji
        if(Mood.Emotion.hasName(intent_emotion)) {
            view_emoji.setImageResource(Mood.Emotion.getImageResourceId(intent_emotion));
            view_scrollView.setBackgroundColor(getResources().getColor(
                    Mood.Emotion.getColorResourceId(intent_emotion)
            ));
        }
        moodDB.getImageFromDB(intent_moodID, view_uploaded_pic);

        // for Mood History: enable view_edit_button button, hide friend_name field
        String page = getIntent().getStringExtra("page");
        if(page == null || page.equals(HomeFragment.class.getSimpleName())) {
            view_edit_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(), EditMoodActivity.class);
                    intent.putExtra("moodID", intent_moodID);
                    intent.putExtra("date",intent_date);
                    intent.putExtra("time",intent_time);
                    intent.putExtra("emotion",intent_emotion);
                    intent.putExtra("reason_text",intent_reason);
                    intent.putExtra("situation",intent_situation);
                    intent.putExtra("location_lat",intent_lat);
                    intent.putExtra("location_lon", intent_lon);
                    intent.putExtra("location_address", intent_address);
                    startActivity(intent);
                }
            });
            view_friend_name.setVisibility(View.GONE);
        }

        // for FriendMood: disable view_edit_button button, show friend_name field
        else {
            view_edit_button.setVisibility(View.GONE);
            String friend_username = getIntent().getStringExtra("friend_username");
            if(friend_username != null) {
                view_friend_name.setVisibility(View.VISIBLE);
                view_friend_name.setText(friend_username);
            }
        }

        view_cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(AppCompatActivity.RESULT_CANCELED);
                finish();
            }
        });

    }
}