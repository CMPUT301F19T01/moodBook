package com.example.moodbook.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.moodbook.DBMoodSetter;
import com.example.moodbook.MainActivity;
import com.example.moodbook.Mood;
import com.example.moodbook.R;

import java.util.HashMap;

import androidx.appcompat.app.AppCompatActivity;

/**
 * This activity is used to display a Mood Object's attribute values and allows the user to edit these values
 * It gets this Mood's fields from the HomeFragment class which passes the needed fields to this activity using an intent
 * @see com.example.moodbook.ui.home.HomeFragment
 * @see Mood
 * @see DBMoodSetter
 * @see MoodEditor
 */
public class EditMoodActivity extends MoodEditorActivity {

    private String moodID;
    private Boolean isImageFitToScreen;

    /**
     * This is a method inherited from the AppCompatActivity
     * @param savedInstanceState
     *  Bundle Object is used to store the data of this activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        moodID = getIntent().getStringExtra("moodID");
        super.createActivity(EditMoodActivity.class.getSimpleName());
    }


    @Override
    protected void initializeViews() {
        isImageFitToScreen = true;
        // emotion
        super.emotion_spinner = findViewById(R.id.edit_emotion_spinner);
        // reason text
        super.reason_editText = findViewById(R.id.edit_reason_editText);
        // reason photo
        super.reason_photo_button = findViewById(R.id.edit_reason_photo_button);
        super.reason_photo_imageView = findViewById(R.id.edit_reason_photo_imageView);
        final HashMap<String, Object> moodMap = new HashMap<>();
        reason_photo_imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isImageFitToScreen) {
                    isImageFitToScreen=false;
                    reason_photo_imageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    reason_photo_imageView.setAdjustViewBounds(true);
                }else{
                    isImageFitToScreen=true;
                    reason_photo_imageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
                    reason_photo_imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                    reason_photo_imageView.setAdjustViewBounds(false);
                }
                if(mood_location != null) {
                    moodMap.put("location_lat",mood_location.getLatitude());
                    moodMap.put("location_lon",mood_location.getLongitude());
                    moodMap.put("location_address", mood_location.getAddress());
                }
                moodDB.editMood(moodID,moodMap);
                Log.i("TEST", "Updated Successful");
                finish();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);

            }
        });
        super.cancel_button = findViewById(R.id.edit_cancel_button);
        // When cancel button is pressed, return to main activity; do nothing
        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(AppCompatActivity.RESULT_CANCELED);
                finish();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
        // situation
        super.situation_spinner = findViewById(R.id.edit_situation_spinner);

        // location
        super.location_button = findViewById(R.id.edit_location_button);

        // action buttons
        super.do_button = findViewById(R.id.edit_save_button);

    }

    @Override
    protected void setupDateTime() {
        TextView show_date_time = findViewById(R.id.show_date_time);

        String intent_date = getIntent().getStringExtra( "date");
        String intent_time = getIntent().getStringExtra("time");
        show_date_time.setText("Created: " + intent_date +" at " + intent_time );
    }

    @Override
    protected void setupReasonPhoto() {
        super.setupReasonPhoto();
        super.moodDB.getImageFromDB(this.moodID, super.reason_photo_imageView);
    }

    @Override
    protected void setupDoButton() {
        super.do_button.setOnClickListener(new View.OnClickListener() {
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
                        moodMap.put("location_address", mood_location.getAddress());
                    }
                    moodDB.editMood(moodID,moodMap);
                    Log.i(TAG, "Mood successfully updated");
                    finish();
                }
            }
        });
    }

}