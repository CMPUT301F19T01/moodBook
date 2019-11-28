package com.example.moodbook.ui.home;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.moodbook.DBMoodSetter;
import com.example.moodbook.LocationPickerActivity;
import com.example.moodbook.Mood;
import com.example.moodbook.MoodInvalidInputException;
import com.example.moodbook.MoodLocation;
import com.example.moodbook.R;
import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;
import java.util.Arrays;

/**
 * This abstract class is used by CreateMoodActivity and EditMoodActivity
 * Provides the base methods for adding/editing mood details
 * This class implements the MoodEditorInterface to provide functionality for the methods declared in that interface
 * This class inherits the attributes and methods from the AppCompatActivity
 */
public abstract class MoodEditorActivity extends AppCompatActivity implements MoodEditor.MoodEditorInterface {

    private static final String[] SUBCLASSES_NAMES = {
            CreateMoodActivity.class.getSimpleName(),
            EditMoodActivity.class.getSimpleName()
    };
    protected String TAG;

    // moodSetter
    protected DBMoodSetter moodDB;

    // emotion
    protected Spinner emotion_spinner;
    private MoodStateAdapter emotionAdapter;
    protected String mood_emotion;

    // reason text
    protected EditText reason_editText;
    protected String mood_reason_text;

    // reason photo
    protected Button reason_photo_button;
    protected ImageView reason_photo_imageView;
    protected Bitmap reason_photo_bitmap;

    // situation
    protected Spinner situation_spinner;
    protected String mood_situation;

    // location
    protected Button location_button;
    protected MoodLocation mood_location;

    // action buttons
    protected Button save_button;
    protected Button cancel_button;


    /**
     * This is a method inherited from the AppCompatActivity
     * @param savedInstanceState Bundle Object is used to stored the data of this activity
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        super.onCreate(savedInstanceState);
    }

    /**
     * This method identifies what activity is calling it, then sets up the necessary layout that it should display.
     * It calls the methods to setup editors of all the mood fields within the activity.
     * @param activityName
     * This is the name of the activity that is calling the method
     */
    protected void createActivity(String activityName) {
        if(!Arrays.asList(SUBCLASSES_NAMES).contains(activityName)){
            throw new IllegalArgumentException(activityName+" is not a permitted subclass!");
        }
        this.TAG = activityName;

        int activityLayoutId = (this.TAG.equals(SUBCLASSES_NAMES[0])) ?
                R.layout.activity_create_mood : R.layout.activity_edit_mood;
        setContentView(activityLayoutId);

        initializeDBMoodSetter();

        initializeViews();

        setupDateTime();
        setupEmotion();
        setupReasonText();
        setupReasonPhoto();
        setupSituation();
        setupLocation();
        setupSaveButton();
        setupCancelButton();
    }

    /**
     * This gets the photo that was taken and let the image be shown in the page
     * @param requestCode An int for requestCode
     * @param resultCode An int for the result Code
     * @param data
     *
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            MoodEditor.getImageResult(requestCode, resultCode, data, reason_photo_imageView, this);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
     *  This is current location of mood event5
     */
    @Override
    public void setMoodLocation(MoodLocation location) {
        this.mood_location = location;
        String add_location_button_text = location.getAddress();
        location_button.setText(
                (add_location_button_text==null)?"PICK MOOD LOCATION":add_location_button_text);
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


    /**
     * This method initialize the database.
     */
    private void initializeDBMoodSetter() {
        // initialize DB connector
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        moodDB = new DBMoodSetter(mAuth, getApplicationContext(), TAG);
    }

    /**
     * Must initialize all the views except date&time in subclass
     */
    protected abstract void initializeViews();

    /**
     * Must setup date & time editors in subclass
     * since data & time editors are different for each subclass activity
     */
    protected abstract void setupDateTime();

    /**
     * This method initializes a MoodStateAdapter for emotional state spinner
     * If state is in the EditMoodActivity, then it will display the emotion for the mood.
     */
    protected void setupEmotion() {
        emotionAdapter = new MoodStateAdapter(this,
                MoodEditor.EMOTION_STATE_LIST, MoodEditor.EMOTION_IMAGE_LIST);
        String intent_emotion = getIntent().getStringExtra("emotion");
        MoodEditor.setEmotionSpinner(this, emotion_spinner, emotionAdapter, intent_emotion);
    }

    /**
     * This method will display the reason for the chosen mood if state is in the EditMoodActivity.
     */
    protected void setupReasonText() {
        String intent_reason = getIntent().getStringExtra("reason_text");
        if(intent_reason != null) {
            reason_editText.setText(intent_reason);
        }
    }

    /**
     * This method allows users to take or choose a photo when a button is clicked.
     */
    protected void setupReasonPhoto() {
        reason_photo_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MoodEditor.setImage(MoodEditorActivity.this);
            }
        });
    }

    /**
     * This method initializes an Array Adapter for situation spinner.
     * If state is in the EditMoodActivity, then it will display the situation for the mood.
     */
    protected void setupSituation() {
        // Initializing an ArrayAdapter for situation spinner
        ArrayAdapter<String> spinnerArrayAdapter = MoodEditor.getSituationAdapter(
                this, R.layout.spinner_situation);
        // intent data is null for CreateMoodActivity
        String intent_situation = getIntent().getStringExtra("situation");
        MoodEditor.setSituationSpinner(this, situation_spinner, spinnerArrayAdapter, intent_situation);
    }

    /**
     * This method displays the location for the chosen mood.
     * This method enables users to add or edit a location.
     */
    protected void setupLocation() {
        // intent data is null for CreateMoodActivity
        String intent_lat = getIntent().getStringExtra("location_lat");
        String intent_lon = getIntent().getStringExtra("location_lon");
        String intent_address = getIntent().getStringExtra("location_address");
        if(intent_lat != null && intent_lon != null) {
            location_button.setText(intent_address);
        }

        location_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // start activity to edit location
                Intent intent = new Intent(getApplicationContext(), LocationPickerActivity.class);
                startActivityForResult(intent, LocationPickerActivity.REQUEST_EDIT_LOCATION);
            }
        });
    }

    /**
     * This method is for saving the changes after editing a mood.
     */
    protected abstract void setupSaveButton();

    /**
     * This method is for canceling going back to previous activity, without saving any changes. 
     */
    protected void setupCancelButton() {
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
     * Checks if mood inputs are valid: emotion & reason_text
     * @return
     *  A boolean with representing all inputs are valid or all inputs are not valid.
     */
    protected boolean validMoodInputs() {
        boolean areInputsValid = true;
        // check mood_emotion if changed
        if(mood_emotion != null) {
            try {
                Mood.parseMoodEmotion(mood_emotion);
            } catch (MoodInvalidInputException e) {
                emotionAdapter.setError(emotion_spinner.getSelectedView(), e.getMessage());
                areInputsValid = false;
            }
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
