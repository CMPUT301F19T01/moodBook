package com.example.moodbook;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import android.content.Intent;
import android.graphics.Bitmap;
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
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;


/**
 * This activity is used to create a Mood Object by clicking on an add button
 * @see com.example.moodbook.ui.home.HomeFragment
 * @see Mood
 * @see DBMoodSetter
 * @see MoodListAdapter
 * @see MoodEditor
 */
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
    private Button add_location_button;
    private Location mood_location;

    //image
    private Bitmap bitImage;

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
                                mood_reason_text,bitImage,mood_situation,mood_location);
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
        MoodEditor.getImageResult(requestCode, resultCode, data, reason_photo_imageView, this);
    }

    /**
     * This method shows the coordinates of a view
     * @deprecated
     */
    public void showCoords(View view){
    }

    // set attributes in Activity

    /**
     * This  is a method inherited from the MoodEditor Interface sets a value for a mood emotion
     * @param emotion
     *   A Mood Object attribute of emotion
     *   @see Mood
     */
    @Override
    public void setMoodEmotion(String emotion) {
        this.mood_emotion = emotion;
    }

    /**
     * This is a method inherited from the MoodEditor Interface sets a value for a mood situation
     * @param situation
     *   A Mood Object attribute of situation
     *   @see Mood
     */

    @Override
    public void setMoodSituation(String situation) {
        this.mood_situation = situation;
    }

    /**
     * This is a method inherited from the MoodEditor Interface sets a value for a mood location
     * @param location
     *     A Mood Object attribute of situation
     *     @see  Mood
     *
     */

    @Override
    public void setMoodLocation(Location location) {
        this.mood_location = location;
        String add_location_button_text = ((Double)location.getLatitude()).toString() + " , "
                + ((Double)location.getLongitude()).toString();
        add_location_button.setText(add_location_button_text);
    }

    /**
     * This is a method inherited from the MoodEditor Interface that sets a value for a bitmap Image
     * @param bitImage
     */
    @Override
    public void setMoodReasonPhoto(Bitmap bitImage) {
        this.bitImage = bitImage;
    }


    /**
     * Initializes the current date
     */
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

    /**
     * Initializes the current time
     */
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

    /**
     * Initializes the emotion picked by the user
     */
    private void initializeEmotion() {
        mood_emotion = null;
        emotion_spinner = findViewById(R.id.create_emotion_spinner);

        // Initializing a MoodStateAdapter for emotional state spinner
        emotionAdapter = new MoodStateAdapter(this,
                MoodEditor.EMOTION_STATE_LIST, MoodEditor.EMOTION_IMAGE_LIST);
        MoodEditor.setEmotionSpinner(this, emotion_spinner, emotionAdapter);
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
        MoodEditor.setSituationSpinner(this, situation_spinner, situationAdapter);
    }
    /**
     * Initializes the user's current location
     */
    private void initializeLocation() {
        add_location_button = findViewById(R.id.create_location_button);

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

    /**
     * Checks if mood inputs are valid
     * @return
     *  A boolean with representing all inputs are valid or all inputs are not valid.
     */

    private boolean validMoodInputs() {
        boolean areInputsValid = true;
        mood_date = add_date_button.getText().toString();
        try {
            Mood.parseMoodDate(mood_date);
            add_date_button.setError(null);
        } catch (MoodInvalidInputException e) {
            add_date_button.setError(e.getMessage());
            areInputsValid = false;
        }
        mood_time = add_time_button.getText().toString();
        try {
            Mood.parseMoodTime(mood_time);
            add_time_button.setError(null);
        } catch (MoodInvalidInputException e) {
            add_time_button.setError(e.getMessage());
            areInputsValid = false;
        }
        try {
            Mood.parseMoodEmotion(mood_emotion);
        } catch (MoodInvalidInputException e) {
            emotionAdapter.setError(emotion_spinner.getSelectedView(), e.getMessage());
            areInputsValid = false;
        }
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