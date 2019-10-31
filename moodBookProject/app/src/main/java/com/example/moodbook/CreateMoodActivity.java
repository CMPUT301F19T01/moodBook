package com.example.moodbook;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.location.Location;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;


public class CreateMoodActivity extends AppCompatActivity implements MoodEditor.MoodInterface{

    // moodSetter
    protected DBMoodSetter moodDB;
    private FirebaseAuth mAuth;

    // date
    private Button add_date_button;
    private String mood_date;

    // time
    private Button add_time_button;
    private String mood_time;

    // emotion
    private Spinner emotion_spinner;
    private MoodStateAdapter emotionAdapter;
    private String mood_emotion;

    // reason text
    private EditText reason_editText;
    private String mood_reason_text;

    // reason photo
    private ImageView reason_photo_imageView;

    // situation
    private String mood_situation;

    // location
    private Location mood_location;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_mood);
        mAuth = FirebaseAuth.getInstance();
        moodDB = new DBMoodSetter(mAuth, getApplicationContext());

        final FragmentManager fm = getSupportFragmentManager();
        final SelectMoodStateFragment s = new SelectMoodStateFragment();

        initializeDate();
        initializeTime();
        initializeEmotion();
        initializeReasonText();
        initializeReasonPhoto();
        initializeSituation();
        initializeLocation();

        final Button add_button = findViewById(R.id.create_add_button);
        final Button cancel_button = findViewById(R.id.create_cancel_button);

        // When this button is clicked, we want to return a result
        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validMoodInputs()){
                    try {
                        Mood newMood = new Mood(mood_date+" "+mood_time,mood_emotion,
                                mood_reason_text,null,mood_situation,mood_location);
                        //moodDB.addMood(newMood);
                        moodDB.addMood(newMood);
                        finish();
                    } catch (MoodInvalidInputException e) {
                        // shouldn't happen since inputs are already checked
                        Toast.makeText(getApplicationContext(),
                                "Adding failed: " + e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                }
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
        MoodEditor.getImageResult(requestCode, resultCode, data, reason_photo_imageView);
    }

    public void showCoords(View view){
    }

    // set attributes in Activity
    @Override
    public void setMoodEmotion(String emotion) {
        this.mood_emotion = emotion;
    }

    @Override
    public void setMoodSituation(String situation) {
        this.mood_situation = situation;
    }

    @Override
    public void setMoodLocation(Location location) {
        this.mood_location = location;
    }


    private void initializeDate() {
        add_date_button = findViewById(R.id.create_date_button);
        // show current date
        add_date_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MoodEditor.showCalendar((Button)view);
            }
        });
    }

    private void initializeTime() {
        add_time_button = findViewById(R.id.create_time_button);
        // show current time
        add_time_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MoodEditor.showTime((Button)view);
            }
        });
    }

    private void initializeEmotion() {
        mood_emotion = null;
        emotion_spinner = findViewById(R.id.create_emotion_spinner);

        // Initializing a MoodStateAdapter for emotional state spinner
        emotionAdapter = new MoodStateAdapter(this,
                MoodEditor.EMOTION_STATE_LIST, MoodEditor.EMOTION_IMAGE_LIST);
        MoodEditor.setEmotionSpinner(this, emotion_spinner, emotionAdapter);
    }

    private void initializeReasonText() {
        reason_editText = findViewById(R.id.create_reason_editText);
    }

    private void initializeReasonPhoto() {
        Button add_reason_photo_button = findViewById(R.id.create_reason_photo_button);
        reason_photo_imageView = findViewById(R.id.create_reason_photo_imageView);

        add_reason_photo_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MoodEditor.setImage(CreateMoodActivity.this);
            }
        });
    }

    private void initializeSituation() {
        Spinner situation_spinner = findViewById(R.id.create_situation_spinner);

        // Initializing an ArrayAdapter for situation spinner
        final ArrayAdapter<String> situationAdapter = MoodEditor.getSituationAdapter(
                this, R.layout.spinner_situation);
        MoodEditor.setSituationSpinner(this, situation_spinner, situationAdapter);
    }

    private void initializeLocation() {
        Button add_location_button = findViewById(R.id.create_location_button);

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
    }

    private boolean validMoodInputs() {
        boolean areInputsValid = true;
        mood_date = add_date_button.getText().toString();
        try {
            Mood.parseMoodDate(mood_date);
            add_date_button.setError(null);
        } catch (MoodInvalidInputException e) {
            /*Toast.makeText(getApplicationContext(),
                    e.getInputType()+": "+e.getMessage(),
                    Toast.LENGTH_SHORT).show();*/
            add_date_button.setError(e.getMessage());
            areInputsValid = false;
        }
        mood_time = add_time_button.getText().toString();
        try {
            Mood.parseMoodTime(mood_time);
            add_time_button.setError(null);
        } catch (MoodInvalidInputException e) {
            /*Toast.makeText(getApplicationContext(),
                    e.getInputType()+": "+e.getMessage(),
                    Toast.LENGTH_SHORT).show();*/
            add_time_button.setError(e.getMessage());
            areInputsValid = false;
        }
        try {
            Mood.parseMoodEmotion(mood_emotion);
        } catch (MoodInvalidInputException e) {
            /*Toast.makeText(getApplicationContext(),
                    e.getInputType()+": "+e.getMessage(),
                    Toast.LENGTH_SHORT).show();*/
            emotionAdapter.setError(emotion_spinner.getSelectedView(), e.getMessage());
            areInputsValid = false;
        }
        mood_reason_text = reason_editText.getText().toString();
        try {
            Mood.parseMoodReasonText(mood_reason_text);
        } catch (MoodInvalidInputException e) {
            /*Toast.makeText(getApplicationContext(),
                    e.getInputType()+": "+e.getMessage(),
                    Toast.LENGTH_SHORT).show();*/
            reason_editText.setError(e.getMessage());
            areInputsValid = false;
        }
        return areInputsValid;
    }

}