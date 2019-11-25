package com.example.moodbook;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.moodbook.ui.friendMood.FriendMoodFragment;
import com.example.moodbook.ui.home.EditMoodActivity;
import com.example.moodbook.ui.home.HomeFragment;
import com.example.moodbook.ui.home.MoodListAdapter;
import com.example.moodbook.ui.myFriendMoodMap.MyFriendMoodMapFragment;
import com.google.firebase.auth.FirebaseAuth;

/**
 * This activity is used to  enable a user to be to view detials for a specific mood
 */
public class ViewMoodActivity extends AppCompatActivity {
    private TextView view_date_time;
    private TextView view_reason;
    private TextView view_situation;
    private TextView view_location;
    private TextView view_emotion;
    private ImageView view_emoji;
    private ImageView view_uploaded_pic;
    private Button edit;
    private Button cancel_view;
    private MoodListAdapter moodAdapter;
    private DBMoodSetter moodDB;
    private FirebaseAuth mAuth;
    private ScrollView linear_emotion;
    private static final String TAG = "DB";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_mood);
        mAuth = FirebaseAuth.getInstance();
        moodDB = new DBMoodSetter(mAuth, getApplicationContext(), TAG);
        final FragmentManager fm = getSupportFragmentManager();
        view_date_time = findViewById(R.id.view_date_time);
        view_reason = findViewById(R.id.view_reason);
        view_situation = findViewById(R.id.view_situation);
        view_location = findViewById(R.id.view_location);
        view_emotion = findViewById(R.id.view_emotion);
        view_emoji = findViewById(R.id.view_emoji);
        edit = findViewById(R.id.edit);
        cancel_view = findViewById(R.id.cancel_view);
        view_uploaded_pic = findViewById(R.id.view_uploaded_pic);


        // Getting and Setting Intents
        final String intent_moodID = getIntent().getStringExtra("moodID");
        final String intent_date = getIntent().getStringExtra( "date");
        final String intent_time = getIntent().getStringExtra("time");
        view_date_time.setText("Created: " + intent_date +" at " + intent_time );
        final String intent_reason = getIntent().getStringExtra("reason_text");
        view_reason.setText("Reason: "+intent_reason);
        final String intent_lat = getIntent().getStringExtra("location_lat");
        final String intent_lon = getIntent().getStringExtra("location_lon");
        final String intent_address = getIntent().getStringExtra("location_address");
        view_location.setText(intent_address);
        final String intent_situation =getIntent().getStringExtra("situation");
        view_situation.setText("Situation:  " + intent_situation);
        final String intent_emotion =getIntent().getStringExtra("emotion");
        view_emotion.setText(intent_emotion);
        linear_emotion = findViewById(R.id.viewPage);
        //Set emoji
        switch(intent_emotion){
            case "sad":
                view_emoji.setImageResource(R.drawable.sad);
                linear_emotion.setBackgroundColor(getResources().getColor(R.color.sadBlue));
                break;
            case "happy":
                view_emoji.setImageResource(R.drawable.happy);
                linear_emotion.setBackgroundColor(getResources().getColor(R.color.happyYellow));
                break;
            case "afraid":
                view_emoji.setImageResource(R.drawable.afraid);
                linear_emotion.setBackgroundColor(getResources().getColor(R.color.afraidBrown));
                break;
            case "angry":
                view_emoji.setImageResource(R.drawable.angry);
                linear_emotion.setBackgroundColor(getResources().getColor(R.color.angryRed));
                break;
        }
        moodDB.getImageFromDB(intent_moodID, view_uploaded_pic);

        // for Mood History: enable edit button
        String page = getIntent().getStringExtra("page");
        if(page == null || page.equals(HomeFragment.class.getSimpleName())) {
            edit.setOnClickListener(new View.OnClickListener() {
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
        }
        // for FriendMood and FriendMoodMap: disable edit button
        else {
            edit.setVisibility(View.GONE);
        }

        cancel_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(AppCompatActivity.RESULT_CANCELED);
                finish();
                startActivity(new Intent(ViewMoodActivity.this,MainActivity.class));
            }
        });

    }



}