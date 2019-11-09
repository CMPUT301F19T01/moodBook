package com.example.moodbook;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.google.firebase.auth.FirebaseAuth;

import java.util.HashMap;

/**
 * This activity is used to display a Mood Object's attribute values and allows the user to edit these values
 * It gets this Mood's fields from the HomeFragment class which passes the needed fields to this activity using an intent
 * @see com.example.moodbook.ui.home.HomeFragment
 * @see DBMoodSetter
 * @see MoodListAdapter
 */
public class EditMoodActivity extends AppCompatActivity implements MoodEditor.MoodInterface {

    private String moodID;
    private DBMoodSetter moodDB;
    private MoodListAdapter moodAdapter;
    private FirebaseAuth mAuth;
    private static final String TAG = "DB";

    // date
    private Button edit_date_button;
    private TextView show_date_time;
    private String intent_date;

    // time
    private Button edit_time_button;
    private String intent_time;

    // emotion
    private String selectedMoodState;
    private Spinner spinner_emotion;
    private MoodStateAdapter emotionAdapter;
    private final String [] emotionStateList = MoodEditor.EMOTION_STATE_LIST;
    private final int[] emotionImages = MoodEditor.EMOTION_IMAGE_LIST;
    private final int[] emotionColors = MoodEditor.EMOTION_COLOR_LIST;
    private String intent_emotion;

    // location
    private Button edit_location_button;
    private Location mood_location;

    // situation
    private Spinner edit_spinner_situation;
    private String mood_situation;
    // initialize string array for situation
    private final String[] situationList = MoodEditor.SITUATION_LIST;

    // reason text
    private EditText edit_text_reason;
    private String intent_reason;

    // reason photo
    private Button edit_photo_button;
    private ImageView image_view_photo;
    public static final int REQUEST_IMAGE = 101;

    private Bitmap bitImage;

    private final String TEST = "test";


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
        moodDB = new DBMoodSetter(mAuth,getApplicationContext(),
                DBMoodSetter.getMoodHistoryListener(moodAdapter), TAG);
        final FragmentManager fm = getSupportFragmentManager();
        //final SelectMoodStateFragment s = new SelectMoodStateFragment();
        edit_photo_button = findViewById(R.id.edit_reason_photo_button);
        image_view_photo = findViewById(R.id.edit_reason_photo_imageView);
        show_date_time = findViewById(R.id.show_date_time);
        edit_spinner_situation = findViewById(R.id.edit_situation_spinner);
        edit_text_reason = findViewById(R.id.edit_reason_editText);
        edit_location_button = findViewById(R.id.edit_location_button);

        //Getting and Setting intents
        final String intent_moodID = getIntent().getStringExtra("moodID");
        String intent_date = getIntent().getStringExtra( "date");
        String intent_time = getIntent().getStringExtra("time");
        show_date_time.setText("Created: " + intent_date +" at " + intent_time );
        String intent_reason = getIntent().getStringExtra("reason_text");
        edit_text_reason.setText(intent_reason);
        String intent_lat = getIntent().getStringExtra("location_lat");
        String intent_lon = getIntent().getStringExtra("location_lon");
        edit_location_button.setText(intent_lat + " , " + intent_lon);
        moodDB.getImageFromDB(intent_moodID, image_view_photo);


        final Button save_button = findViewById(R.id.edit_save_button);
        final Button cancel_edit_button = findViewById(R.id.edit_cancel_button);

        // Initializing an ArrayAdapter for situation spinner
        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                this, R.layout.spinner_situation, situationList){
            @Override
            public boolean isEnabled(int position){
                return (position != 0);
            }
            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView text = (TextView) view;
                text.setTextColor((position == 0) ? Color.GRAY : Color.BLACK);
                return view;
            }
        };
        String intent_situation = getIntent().getStringExtra("situation");
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_situation);
        edit_spinner_situation.setAdapter(spinnerArrayAdapter);
        edit_spinner_situation.setSelection(spinnerArrayAdapter.getPosition(intent_situation));
        edit_spinner_situation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position > 0){
                    String selectedSituation = (String) parent.getItemAtPosition(position);
                    setMoodSituation(selectedSituation);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Do nothing
            }
        });

        // Initializing a MoodStateAdapter for emotional state spinner
        String intent_emotion = getIntent().getStringExtra("emotion");
        spinner_emotion = findViewById(R.id.edit_emotion_spinner);
        emotionAdapter = new MoodStateAdapter(this, emotionStateList, emotionImages );
        spinner_emotion.setAdapter(emotionAdapter);
        spinner_emotion.setSelection( emotionAdapter.getPosition(intent_emotion));
        spinner_emotion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getApplicationContext(), emotionStateList[i], Toast.LENGTH_LONG)
                        .show();
                selectedMoodState = emotionStateList[i];
                spinner_emotion.setBackgroundColor(getResources().getColor(emotionColors[i]));
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Do nothing
            }
        });
        initializeReasonPhoto();
        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final HashMap<String, Object> moodMap = new HashMap<>();
                moodMap.put("emotion",spinner_emotion.getSelectedItem().toString());
                moodMap.put("reason_text",edit_text_reason.getText().toString());
                if(mood_situation != null) {
                    moodMap.put("situation",mood_situation);
                }
                if(mood_location != null) {
                    moodMap.put("location_lat",mood_location.getLatitude());
                    moodMap.put("location_lon",mood_location.getLongitude());
                }
                moodDB.editMood(intent_moodID,moodMap);
                Log.i(TEST, "Updated Successful");
                finish();

            }
        });

        // When cancel button is pressed, return to main activity; do nothing
        cancel_edit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(AppCompatActivity.RESULT_CANCELED);
                finish();
            }
        });

        // set the button onClickListener to request location
        edit_location_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // start activity to edit location
                Intent intent = new Intent(getApplicationContext(), LocationPickerActivity.class);
                startActivityForResult(intent, 1);


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
    @SuppressLint("MissingSuperCall")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        MoodEditor.getImageResult(requestCode, resultCode, data, image_view_photo, this);
        MoodEditor.getLocationResult(requestCode, resultCode, data, this);
    }

    /**
     * This allows a user to edit a mood image.
     */
    private void initializeReasonPhoto() {
        image_view_photo = findViewById(R.id.edit_reason_photo_imageView);

        edit_photo_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MoodEditor.setImage(EditMoodActivity.this);
            }
        });
    }

    /**
     * This  is a method inherited from the MoodEditor Interface sets a value for a mood emotion
     * @param emotion
     *   A Mood Object attribute of emotion
     *   @see Mood
     */
    @Deprecated
    @Override
    public void setMoodEmotion(String emotion) {
        // do nothing
    }

    /**
     * This is a method inherited from the MoodEditor Interface sets a value for a mood situation
     * @param situation
     *   A Mood Object attribute of situation
     *   @see Mood
     */

     /* This override MoodEditor.MoodInterface setMoodSituation(),
     * and is setter for mood_situation
     * @param situation
     *  This is current social situation of mood event
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
//    @Ov erride
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
     * This is a method inherited from the MoodEditor Interface that sets a value for a bitmap Image
     * @param bitImage
     *  This is a bitmap image
     * This override MoodEditor.MoodInterface setMoodReasonPhoto(),
     * and is setter for bitImage
     * @param bitImage
     *  This is current reason photo of mood event
     */
    @Override
    public void setMoodReasonPhoto(Bitmap bitImage) {
        this.bitImage = bitImage;
    }

}