package com.example.moodbook;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import android.widget.ArrayAdapter;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import android.widget.Spinner;
import android.widget.Toast;

import com.google.common.collect.ObjectArrays;
import com.google.common.primitives.Ints;


public class CreateMoodActivity extends AppCompatActivity implements MoodEditor.MoodActivity{

    // date
    Button add_date_button;

    // time
    Button add_time_button;

    // emotion
    String selectedMoodState;
    Spinner spinner_emotion;
    MoodStateAdapter emotionAdapter;
    final String [] emotionStateList = ObjectArrays.concat(
            new String[]{"Pick mood state ..."}, Mood.Emotion.getNames(), String.class);
    final int[] emotionImages = Ints.concat(
            new int[]{R.color.transparent}, Mood.Emotion.getImageResources());
    final int[] emotionColors = Ints.concat(
            new int[]{R.color.transparent}, Mood.Emotion.getColorResources());

    // location
    Button add_location_button;

    // situation
    Spinner spinner_situation;
    // initialize string array for situation
    final String[] situationList = new String[]{
            "Add situation ...",
            "Alone",
            "With one person",
            "With two and more",
            "With a crowd"
    };

    // reason text
    EditText edit_text_reason;

    // reason photo
    Button add_photo_button;
    ImageView image_view_photo;

    private String mood_date, mood_time, mood_reason, mood_situation;
    private double mood_lat, mood_lon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_mood);

        final FragmentManager fm = getSupportFragmentManager();
        final SelectMoodStateFragment s = new SelectMoodStateFragment();

        add_photo_button = findViewById(R.id.pick_mood_photo);
        image_view_photo = findViewById(R.id.fill_mood_photo);
        add_date_button = findViewById(R.id.pick_mood_date);
        add_time_button = findViewById(R.id.pick_mood_time);
        spinner_situation = findViewById(R.id.pick_mood_situation);
        edit_text_reason = findViewById(R.id.pick_mood_reason);
        add_location_button = findViewById(R.id.pick_mood_location);

        final Button add_button = findViewById(R.id.add_mood_button);
        final Button cancel_button = findViewById(R.id.cancel_mood_button);

        // Initializing an ArrayAdapter for situation spinner
        final ArrayAdapter<String> situationAdapter = MoodEditor.getSituationAdapter(
                this, R.layout.spinner_situation, situationList);
        MoodEditor.setSituationSpinner(this, spinner_situation, situationAdapter);


        // Initializing a MoodStateAdapter for emotional state spinner
        spinner_emotion = findViewById(R.id.mood_spinner);
        emotionAdapter = new MoodStateAdapter(this, emotionStateList, emotionImages);
        MoodEditor.setEmotionSpinner(this, spinner_emotion, emotionAdapter,
                emotionStateList, emotionColors);


        // Sets mood photo
        add_photo_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MoodEditor.setImage(CreateMoodActivity.this);
            }
        });


        // Sets date, time
        // handles selecting a calendar
        add_date_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MoodEditor.showCalendar((Button)view,CreateMoodActivity.this);
            }
        });
        add_time_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MoodEditor.showTime((Button)view,CreateMoodActivity.this);
            }
        });


        // Gets users location
        // create location manager and listener
        final LocationManager locationManager = MoodEditor.getLocationManager(this);
        final LocationListener locationListener = MoodEditor.getLocationListener(this);


        // set the button onClickListener to request location
        add_location_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MoodEditor.getLocationResult(CreateMoodActivity.this,
                        locationManager, locationListener);
            }
        });


        // When this button is clicked, we want to return a result
        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mood_date = add_date_button.getText().toString();
                mood_time = add_time_button.getText().toString();
                mood_reason = edit_text_reason.getText().toString();
                mood_situation = spinner_situation.getSelectedItem().toString();
                Toast.makeText
                        (getApplicationContext(), "Selected : " + mood_situation, Toast.LENGTH_SHORT)
                        .show();
            }
        });


        // When cancel button is pressed, return to main activity; do nothing
        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(AppCompatActivity.RESULT_CANCELED);
                finish();
            }
        });
    }


    // gets the photo that was taken and let the image be shown in the page
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        MoodEditor.getImageResult(requestCode, resultCode, data, image_view_photo);
    }

    public void showCoords(View view){

    }


    @Override
    public void setSelectedMoodState(String moodState) {
        this.selectedMoodState = moodState;
    }
}