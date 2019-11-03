package com.example.moodbook;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
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

import com.google.firebase.auth.FirebaseAuth;

import java.util.HashMap;

/**
 * This activity is used to display a Mood Object's attribute values and allows the user to edit these values
 * It gets this Mood's fields from the HomeFragment class which passes the needed fields to this activity using an intent
 * @see com.example.moodbook.ui.home.HomeFragment
 * @see DBMoodSetter
 * @see MoodListAdapter
 */
public class EditMoodActivity extends AppCompatActivity {

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
    private String intent_location;

    // situation
    private Spinner edit_spinner_situation;
    private String intent_situation;
    // initialize string array for situation
    private final String[] situationList = MoodEditor.SITUATION_LIST;

    // reason text
    private EditText edit_text_reason;
    private String intent_reason;

    // reason photo
    private Button edit_photo_button;
    private ImageView image_view_photo;
    public static final int REQUEST_IMAGE = 101;

    //location
    private String date_mood, time_mood, reason_mood, situation_mood;
    private double lat_mood, lon_mood;
    private String intent_lat, intent_lon;

    private Bitmap obtainedImg;


    /**
     * This is a method inherited from the AppCompatActivity
     * @param savedInstanceState
     *  Bundle Object is used to stored the data of this activity
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
        final SelectMoodStateFragment s = new SelectMoodStateFragment();
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
        edit_location_button.setText(intent_lat + " , " +intent_lon);
        moodDB.getImageFromDB(intent_moodID, image_view_photo);
        //obtainedImg = moodDB.getImageFromDB(intent_moodID);
        //image_view_photo.setImageBitmap(obtainedImg);
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
                String selectedItemText = (String) parent.getItemAtPosition(position);
                if(position > 0){
                    Toast.makeText(getApplicationContext(),
                            "Selected : " + selectedItemText, Toast.LENGTH_SHORT)
                            .show();
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

        // Sets mood photo
        edit_photo_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MoodEditor.setImage(EditMoodActivity.this);
            }
        });
        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final HashMap<String, Object> moodMap = new HashMap<>();
                moodMap.put("reason_text",edit_text_reason.getText().toString());
                moodMap.put("situation",edit_spinner_situation.getSelectedItem().toString());
                moodMap.put("emotion",spinner_emotion.getSelectedItem().toString());

                moodDB.editMood(intent_moodID,moodMap);
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
        // Gets users location
        // create location manager and listener
        final LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        final LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                lat_mood = location.getLatitude();
                lon_mood = location.getLongitude();
                Toast.makeText(getApplicationContext(), lat_mood + "   " + lon_mood, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {} // not implemented

            @Override
            public void onProviderEnabled(String s) {} // not implemented

            @Override
            public void onProviderDisabled(String s) {} // not implemented
        };

        // set criteria for accuracy of location provider
        final Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setHorizontalAccuracy(Criteria.ACCURACY_HIGH);

        // set to null because we are required to supply looper but not going to use it
        final Looper looper = null;

        // set the button onClickListener to request location
        edit_location_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // ask user for permission to get location
                if (ActivityCompat.checkSelfPermission(EditMoodActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(EditMoodActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(EditMoodActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                    return;
                } else { // permission granted
                    locationManager.requestSingleUpdate(criteria, locationListener, looper);
                }
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
        MoodEditor.getImageResult(requestCode, resultCode, data, image_view_photo, this);
    }

}