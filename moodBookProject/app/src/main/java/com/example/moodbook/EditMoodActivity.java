package com.example.moodbook;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
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
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;

import android.widget.ArrayAdapter;

import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;

import android.widget.RelativeLayout;

import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import javax.microedition.khronos.egl.EGLDisplay;

public class EditMoodActivity extends AppCompatActivity {

    // date
    private Button edit_date_button;

    // time
    private Button edit_time_button;

    // emotion
    private String selectedMoodState;
    private Spinner spinner_emotion;
    private MoodStateAdapter emotionAdapter;
    private final String [] emotionStateList = MoodEditor.EMOTION_STATE_LIST;
    private final int[] emotionImages = MoodEditor.EMOTION_IMAGE_LIST;
    private final int[] emotionColors = MoodEditor.EMOTION_COLOR_LIST;

    // location
    private Button edit_location_button;

    // situation
    private Spinner edit_spinner_situation;
    // initialize string array for situation
    private final String[] situationList = MoodEditor.EMOTION_STATE_LIST;

    // reason text
    private EditText edit_text_reason;

    // reason photo
    private Button edit_photo_button;
    private ImageView image_view_photo;
    public static final int REQUEST_IMAGE = 101;

    private String date_mood, time_mood, reason_mood, situation_mood;
    private double lat_mood, lon_mood;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_layout);

        final FragmentManager fm = getSupportFragmentManager();
        final SelectMoodStateFragment s = new SelectMoodStateFragment();

        edit_photo_button = findViewById(R.id.edit_mood_photo);
        image_view_photo = findViewById(R.id.fill_edit_photo);
        edit_date_button = findViewById(R.id.pick_edit_date);
        edit_time_button = findViewById(R.id.pick_edit_time);
        edit_spinner_situation = findViewById(R.id.edit_mood_situation);
        edit_text_reason = findViewById(R.id.edit_mood_reason);
        edit_location_button = findViewById(R.id.edit_mood_location);

        final Button save_button = findViewById(R.id.save_mood_button);
        final Button cancel_edit_button = findViewById(R.id.cancel_edit_button);

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
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_situation);
        edit_spinner_situation.setAdapter(spinnerArrayAdapter);
        edit_spinner_situation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItemText = (String) parent.getItemAtPosition(position);
                // first item disabled
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
        spinner_emotion = findViewById(R.id.edit_spinner);
        emotionAdapter = new MoodStateAdapter(this, emotionStateList, emotionImages );
        spinner_emotion.setAdapter(emotionAdapter);
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

        // Sets date, time
        // handles selecting a calendar
        edit_date_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MoodEditor.showCalendar((Button)view,EditMoodActivity.this);
            }
        });
        edit_time_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MoodEditor.showTime((Button)view,EditMoodActivity.this);
            }
        });

        // When this button is clicked, we want to return a result
        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                date_mood = edit_date_button.getText().toString();
                time_mood = edit_time_button.getText().toString();
                reason_mood = edit_text_reason.getText().toString();
                situation_mood = edit_spinner_situation.getSelectedItem().toString();
                Toast.makeText
                        (getApplicationContext(), "Selected : " + situation_mood, Toast.LENGTH_SHORT)
                        .show();
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

    // gets the photo that was taken and let the image be shown in the page
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        MoodEditor.getImageResult(requestCode, resultCode, data, image_view_photo);
    }

    public void showCoords(View view){

    }
}