package com.example.moodbook;

import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

/**
 * This activity is used by Mood History to create a Mood Object when clicking an add button
 * @see com.example.moodbook.ui.home.HomeFragment
 * @see Mood
 * @see DBMoodSetter
 * @see MoodEditor
 */
public class CreateMoodActivity extends AppCompatActivity implements MoodEditor.MoodInterface{

    private static final String TAG = CreateMoodActivity.class.getSimpleName();

    // moodSetter
    private DBMoodSetter moodDB;

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
    private Bitmap reason_photo_bitmap;

    // situation
    private String mood_situation;

    // location
    private Button add_location_button;
    private Location mood_location;


    /**
     * This is a method inherited from the AppCompatActivity
     * @param savedInstanceState
     *  Bundle Object is used to stored the data of this activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_mood);

        initializeDBMoodSetter();

        initializeDateTime();
        initializeEmotion();
        initializeReasonText();
        initializeReasonPhoto();
        initializeSituation();
        initializeLocation();

        initializeDoButton();
        initializeCancelButton();
    }


    /**
     * This gets the photo that was taken and let the image be shown in the page
     * @param requestCode
     *   An int for requestCode
     * @param resultCode
     *   An int for the result Code
     * @param data
     *
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        MoodEditor.getImageResult(requestCode, resultCode, data, reason_photo_imageView, this);
        MoodEditor.getLocationResult(requestCode, resultCode, data, this);
    }


    /**
     * This override MoodEditor.MoodInterface setMoodEmotion(),
     * and is setter for mood_emotion
     * @param emotion
     *  This is current social situation of mood event
     */
    @Override
    public void setMoodEmotion(String emotion) {
        this.mood_emotion = emotion;
    }

    /**
     * This override MoodEditor.MoodInterface setMoodSituation(),
     * and is setter for mood_situation
     * @param situation
     *  This is current social situation of mood event
     */
    @Override
    public void setMoodSituation(String situation) {
        this.mood_situation = situation;
    }

    /**
     * This override MoodEditor.MoodInterface setMoodLocation(),
     * and is setter for mood_location, as well as updating location button text with current location
     * @param location
     *  This is current location of mood event
     */
    @Override
    public void setMoodLocation(Location location) {
        this.mood_location = location;
        String add_location_button_text = ((Double)location.getLatitude()).toString() + " , "
                + ((Double)location.getLongitude()).toString();
        add_location_button.setText(add_location_button_text);
    }

    /**
     * This override MoodEditor.MoodInterface setMoodReasonPhoto(),
     * and is setter for bitImage
     * @param bitmap
     *  This is current reason photo of mood event
     */
    @Override
    public void setMoodReasonPhoto(Bitmap bitmap) {
        this.reason_photo_bitmap = bitmap;
    }


    private void initializeDBMoodSetter() {
        // initialize DB connector
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        moodDB = new DBMoodSetter(mAuth, getApplicationContext(), TAG);
    }


    /**
     * Initializes the current date and time
     */
    private void initializeDateTime() {
        // date
        add_date_button = findViewById(R.id.create_date_button);
        MoodEditor.showDate(add_date_button);

        add_date_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MoodEditor.showDate(add_date_button);
            }
        });

        // time
        add_time_button = findViewById(R.id.create_time_button);
        MoodEditor.showTime(add_time_button);

        add_time_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MoodEditor.showTime(add_time_button);
            }
        });
    }

    /**
     * Initializes the emotion picked by the user
     */
    private void initializeEmotion() {
        mood_emotion = null;
        emotion_spinner = findViewById(R.id.create_emotion_spinner);

        // Initializing a MoodStateAdapter for emotional state spinner
        emotionAdapter = new MoodStateAdapter(this,
                MoodEditor.EMOTION_STATE_LIST, MoodEditor.EMOTION_IMAGE_LIST);
        MoodEditor.setEmotionSpinner(this, emotion_spinner, emotionAdapter,null);
    }

    /**
     * Initializes the reason typed by the user
     */
    private void initializeReasonText() {
        reason_editText = findViewById(R.id.create_reason_editText);
    }

    /**
     * Initializes the user's photo
     */
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

    /**
     * Initializes the situation picked by the user
     */
    private void initializeSituation() {
        Spinner situation_spinner = findViewById(R.id.create_situation_spinner);

        // Initializing an ArrayAdapter for situation spinner
        final ArrayAdapter<String> situationAdapter = MoodEditor.getSituationAdapter(
                this, R.layout.spinner_situation);
        MoodEditor.setSituationSpinner(this, situation_spinner, situationAdapter,null);
    }

    /**
     * Initializes the user's current location
     */
    private void initializeLocation() {
        add_location_button = findViewById(R.id.create_location_button);

        // set the button onClickListener to request location
        add_location_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // start activity to edit location
                Intent intent = new Intent(getApplicationContext(), LocationPickerActivity.class);
                startActivityForResult(intent, LocationPickerActivity.REQUEST_EDIT_LOCATION);
            }
        });
    }

    private void initializeDoButton() {
        Button add_button = findViewById(R.id.create_add_button);
        // When this button is clicked, return a result
        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validMoodInputs()){
                    try {
                        Mood newMood = new Mood(mood_date+" "+mood_time,mood_emotion,
                                mood_reason_text,reason_photo_bitmap,mood_situation,mood_location);
                        moodDB.addMood(newMood);
                        Log.i(TAG, "Mood successfully added");
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
    }

    private void initializeCancelButton() {
        Button cancel_button = findViewById(R.id.create_cancel_button);
        // When cancel button is pressed, return to main activity; do nothing
        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(AppCompatActivity.RESULT_CANCELED);
                finish();
            }
        });
    }

    /**
     * Checks if mood inputs are valid
     * @return
     *  A boolean with representing all inputs are valid or all inputs are not valid.
     */
    private boolean validMoodInputs() {
        boolean areInputsValid = true;
        // get and check mood_date
        mood_date = add_date_button.getText().toString();
        try {
            Mood.parseMoodDate(mood_date);
            add_date_button.setError(null);
        } catch (MoodInvalidInputException e) {
            add_date_button.setError(e.getMessage());
            areInputsValid = false;
        }
        // get and check mood_time
        mood_time = add_time_button.getText().toString();
        try {
            Mood.parseMoodTime(mood_time);
            add_time_button.setError(null);
        } catch (MoodInvalidInputException e) {
            add_time_button.setError(e.getMessage());
            areInputsValid = false;
        }
        // check mood_emotion
        try {
            Mood.parseMoodEmotion(mood_emotion);
        } catch (MoodInvalidInputException e) {
            emotionAdapter.setError(emotion_spinner.getSelectedView(), e.getMessage());
            areInputsValid = false;
        }
        // get and check mood_reason_text
        mood_reason_text = reason_editText.getText().toString();
        try {
            Mood.parseMoodReasonText(mood_reason_text);
        } catch (MoodInvalidInputException e) {
            reason_editText.setError(e.getMessage());
            areInputsValid = false;
        }
        return areInputsValid;
    }

}