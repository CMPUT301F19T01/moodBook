package com.example.moodbook.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.moodbook.DBMoodSetter;
import com.example.moodbook.Mood;
import com.example.moodbook.MoodInvalidInputException;
import com.example.moodbook.R;

/**
 * This activity is used by Mood History to create a Mood Object when clicking an add button
 * @see com.example.moodbook.ui.home.HomeFragment
 * @see Mood
 * @see DBMoodSetter
 * @see MoodEditor
 */
public class CreateMoodActivity extends MoodEditorActivity {

    // date
    private Button add_date_button;
    private String mood_date;

    // time
    private Button add_time_button;
    private String mood_time;
    private boolean isImageFitToScreen;


    /**
     * This is a method inherited from the AppCompatActivity
     * @param savedInstanceState
     *  Bundle Object is used to stored the data of this activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.createActivity(CreateMoodActivity.class.getSimpleName());
    }


    @Override
    protected void initializeViews() {
        isImageFitToScreen = true;
        // emotion
        super.mood_emotion = null;
        super.emotion_spinner = findViewById(R.id.create_emotion_spinner);
        // reason text
        super.reason_editText = findViewById(R.id.create_reason_editText);
        // reason photo
        super.reason_photo_button = findViewById(R.id.create_reason_photo_button);
        super.reason_photo_imageView = findViewById(R.id.create_reason_photo_imageView);

        // when imageview is clicked, then it would enlarge the image
        reason_photo_imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isImageFitToScreen) {
                    isImageFitToScreen=false;
                    reason_photo_imageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    reason_photo_imageView.setAdjustViewBounds(true);
                }else{
                    isImageFitToScreen=true;
                    reason_photo_imageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    reason_photo_imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                    reason_photo_imageView.setAdjustViewBounds(false);
                }
            }
        });
        // situation
        super.situation_spinner = findViewById(R.id.create_situation_spinner);
        // location
        super.location_button = findViewById(R.id.create_location_button);

        // action buttons
        super.save_button = findViewById(R.id.create_add_button);
        super.cancel_button = findViewById(R.id.create_cancel_button);
    }

    @Override
    protected void setupDateTime() {
        // date
        this.add_date_button = ((AppCompatActivity)this).findViewById(R.id.create_date_button);
        Log.d(TAG, ""+add_date_button.getId());
        MoodEditor.showDate(this.add_date_button);

        this.add_date_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MoodEditor.showDate(CreateMoodActivity.this.add_date_button);
            }
        });

        // time
        this.add_time_button = ((AppCompatActivity)this).findViewById(R.id.create_time_button);
        MoodEditor.showTime(this.add_time_button);

        this.add_time_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MoodEditor.showTime(CreateMoodActivity.this.add_time_button);
            }
        });
    }

    @Override
    protected void setupSaveButton() {
        // When this button is clicked, return a result
        super.save_button.setOnClickListener(new View.OnClickListener() {
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

    /**
     * Checks if mood inputs are valid, including date & time
     * @return
     *  A boolean with representing all inputs are valid or all inputs are not valid.
     */
    @Override
    protected boolean validMoodInputs() {
        boolean areInputsValid = super.validMoodInputs();
        // check date & time if emotion & reason_text are valid
        if(areInputsValid) {
            // get and check mood_date
            this.mood_date = this.add_date_button.getText().toString();
            try {
                Mood.parseMoodDate(this.mood_date);
                this.add_date_button.setError(null);
            } catch (MoodInvalidInputException e) {
                this.add_date_button.setError(e.getMessage());
                areInputsValid = false;
            }
            // get and check mood_time
            this.mood_time = this.add_time_button.getText().toString();
            try {
                Mood.parseMoodTime(this.mood_time);
                this.add_time_button.setError(null);
            } catch (MoodInvalidInputException e) {
                this.add_time_button.setError(e.getMessage());
                areInputsValid = false;
            }
        }
        return areInputsValid;
    }

}