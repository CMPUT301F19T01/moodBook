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
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import java.util.HashMap;

/**
 * This activity is used to display a Mood Object's attribute values and allows the user to edit these values
 * It gets this Mood's fields from the HomeFragment class which passes the needed fields to this activity using an intent
 * @see com.example.moodbook.ui.home.HomeFragment
 * @see Mood
 * @see DBMoodSetter
 * @see MoodEditor
 */
public class EditMoodActivity extends AppCompatActivity implements MoodEditor.MoodInterface {

    private static final String TAG = EditMoodActivity.class.getSimpleName();

    private String moodID;

    // moodSetter
    private DBMoodSetter moodDB;
    private FirebaseAuth mAuth;

    // date_time
    private TextView show_date_time;

    // emotion
    private Spinner emotion_spinner;
    private MoodStateAdapter emotionAdapter;
    private String mood_emotion;

    // reason text
    private EditText reason_editText;
    private String mood_reason_text;

    // reason photo
    private Button edit_photo_button;
    private ImageView reason_photo_imageView;
    private Bitmap reason_photo_bitmap;

    // situation
    private Spinner situation_spinner;
    private String mood_situation;

    // location
    private Button edit_location_button;
    private Location mood_location;


    /**
     * This is a method inherited from the AppCompatActivity
     * @param savedInstanceState
     *  Bundle Object is used to store the data of this activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_mood);

        // initialize DB connector
        mAuth = FirebaseAuth.getInstance();
        moodDB = new DBMoodSetter(mAuth, getApplicationContext(), TAG);

        moodID = getIntent().getStringExtra("moodID");

        initializeDateTime();
        initializeEmotion();
        initializeReasonText();
        initializeReasonPhoto();
        initializeSituation();
        initializeLocation();

        final Button save_button = findViewById(R.id.edit_save_button);
        final Button cancel_button = findViewById(R.id.edit_cancel_button);

        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validMoodInputs()){
                    final HashMap<String, Object> moodMap = new HashMap<>();
                    // update emotion if changed
                    if(mood_emotion != null) {
                        moodMap.put("emotion", mood_emotion);
                    }
                    // update reason_text
                    moodMap.put("reason_text",reason_editText.getText().toString());
                    // update situation if changed
                    if(mood_situation != null) {
                        moodMap.put("situation",mood_situation);
                    }
                    // update location if changed
                    if(mood_location != null) {
                        moodMap.put("location_lat",mood_location.getLatitude());
                        moodMap.put("location_lon",mood_location.getLongitude());
                    }
                    moodDB.editMood(moodID,moodMap);
                    Log.i(TAG, "Mood successfully updated");
                    finish();
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
        String edit_location_button_text = ((Double)location.getLatitude()).toString() + " , "
                + ((Double)location.getLongitude()).toString();
        edit_location_button.setText(edit_location_button_text);
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


    private void initializeDateTime() {
        show_date_time = findViewById(R.id.show_date_time);

        String intent_date = getIntent().getStringExtra( "date");
        String intent_time = getIntent().getStringExtra("time");
        show_date_time.setText("Created: " + intent_date +" at " + intent_time );
    }

    private void initializeEmotion() {
        emotion_spinner = findViewById(R.id.edit_emotion_spinner);

        String intent_emotion = getIntent().getStringExtra("emotion");
        // Initializing a MoodStateAdapter for emotional state spinner
        emotionAdapter = new MoodStateAdapter(this,
                MoodEditor.EMOTION_STATE_LIST, MoodEditor.EMOTION_IMAGE_LIST);
        MoodEditor.setEmotionSpinner(this, emotion_spinner, emotionAdapter, intent_emotion);
    }

    private void initializeReasonText() {
        reason_editText = findViewById(R.id.edit_reason_editText);

        String intent_reason = getIntent().getStringExtra("reason_text");
        reason_editText.setText(intent_reason);
    }

    /**
     * This allows a user to edit a mood image.
     */
    private void initializeReasonPhoto() {
        edit_photo_button = findViewById(R.id.edit_reason_photo_button);
        reason_photo_imageView = findViewById(R.id.edit_reason_photo_imageView);

        moodDB.getImageFromDB(moodID, reason_photo_imageView);

        edit_photo_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MoodEditor.setImage(EditMoodActivity.this);
            }
        });
    }

    private void initializeSituation() {
        situation_spinner = findViewById(R.id.edit_situation_spinner);

        String intent_situation = getIntent().getStringExtra("situation");
        // Initializing an ArrayAdapter for situation spinner
        final ArrayAdapter<String> spinnerArrayAdapter = MoodEditor.getSituationAdapter(
                this, R.layout.spinner_situation);
        MoodEditor.setSituationSpinner(this, situation_spinner, spinnerArrayAdapter, intent_situation);
    }

    private void initializeLocation() {
        edit_location_button = findViewById(R.id.edit_location_button);

        String intent_lat = getIntent().getStringExtra("location_lat");
        String intent_lon = getIntent().getStringExtra("location_lon");
        edit_location_button.setText(intent_lat + " , " + intent_lon);

        // set the button onClickListener to request location
        edit_location_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // start activity to edit location
                Intent intent = new Intent(getApplicationContext(), LocationPickerActivity.class);
                startActivityForResult(intent, LocationPickerActivity.REQUEST_EDIT_LOCATION);
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