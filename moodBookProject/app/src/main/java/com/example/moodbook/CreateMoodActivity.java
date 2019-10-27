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

public class CreateMoodActivity extends AppCompatActivity {


    Button pick_mood;
    Button add_photo;
    ImageView view_photo;
    ImageView emoji;
    TextView mood_state;
    Button add_date;
    Button add_time;
    Button add_location;
    String SelectedMoodState;
    Spinner sp1;
    RelativeLayout pickState;
    MoodStateAdapter adapter;
    String [] states ={"Afraid","Angry","Happy","Sad"};
    int[] images = {R.drawable.afraid, R.drawable.angry,R.drawable.happy,R.drawable.sad};
    int[] colors = {R.color.afraidBrown,R.color.angryRed,R.color.happyYellow,R.color.sadBlue};

//    private Button pick_mood, add_photo, add_date, add_time;
//    private ImageView view_photo, emoji;
//    private TextView mood_state;
    private EditText add_reason;

    private int year, month, day, hour, minute;
    private Spinner situation;
    private static final int REQUEST_IMAGE = 101;
    private String date_mood, time_mood, reason_mood, situation_mood;
    private double lat_mood, lon_mood;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_mood);
        final FragmentManager fm = getSupportFragmentManager();
        final SelectMoodStateFragment s = new SelectMoodStateFragment();
        add_photo = findViewById(R.id.pick_mood_photo);
        view_photo = findViewById(R.id.fill_mood_photo);
        add_date = findViewById(R.id.pick_mood_date);
        add_time = findViewById(R.id.pick_mood_time);
        situation = (Spinner) findViewById(R.id.pick_mood_situation);
        add_reason = findViewById(R.id.pick_mood_reason);
        add_location = findViewById(R.id.pick_mood_location);
        final Button add_button = findViewById(R.id.add_mood_button);
        final Button cancel_button = findViewById(R.id.cancel_mood_button);


        //initialize string array for situation
        String[] option_sit = new String[]{
                "Add situation...",
                "Alone",
                "With one person",
                "With two and more",
                "With a crowd"
        };

        final List<String> listSituation = new ArrayList<>(Arrays.asList(option_sit));

        // Initializing an ArrayAdapter
        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                this,R.layout.spinner_situation,listSituation){
            @Override
            public boolean isEnabled(int position){
                if(position == 0) {
                    return false;
                }
                else {
                    return true;
                }
            }
            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView text = (TextView) view;
                if(position == 0){
                    text.setTextColor(Color.GRAY);
                }
                else {
                    text.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_situation);
        situation.setAdapter(spinnerArrayAdapter);
        situation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItemText = (String) parent.getItemAtPosition(position);
                // first item disabled
                if(position > 0){
                    Toast.makeText
                            (getApplicationContext(), "Selected : " + selectedItemText, Toast.LENGTH_SHORT)
                            .show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        sp1 = (Spinner)findViewById(R.id.mood_spinner);
        adapter = new MoodStateAdapter(this,states,images );
        sp1.setAdapter(adapter);
        sp1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getApplicationContext(),states[i],Toast.LENGTH_LONG).show();
                SelectedMoodState = states[i];
                sp1.setBackgroundColor(getResources().getColor(colors[i]));
//               pickState.setBackgroundColor(getResources().getColor(colors[i]));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {


            }
        });


        //sets mood photo
        add_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setImage(view);
            }
        });

        // sets date, time
        //handles selecting a calendar
        add_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCalendar(view);
            }
        });
        add_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTime(view);
            }
        });

        // When this button is clicked, we want to return a result
        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                date_mood = add_date.getText().toString();
                time_mood = add_time.getText().toString();
                reason_mood = add_reason.getText().toString();
                situation_mood = situation.getSelectedItem().toString();
                Toast.makeText
                        (getApplicationContext(), "Selected : " + situation_mood, Toast.LENGTH_SHORT)
                        .show();
            }
        });

        //when cancel button is pressed, return to main activity; do nothing
        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(AppCompatActivity.RESULT_CANCELED);
                finish();
            }
        });

        // gets users location
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
        add_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // ask user for permission to get location
                if (ActivityCompat.checkSelfPermission(CreateMoodActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(CreateMoodActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(CreateMoodActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                    return;


                }else{ // permission granted
                    locationManager.requestSingleUpdate(criteria, locationListener, looper);

                }
            }
        });


    }

    // for showing calendar so user could select a date
    public void showCalendar(View view){
        final Button dateFilled = (Button) view;
        Calendar calendar = Calendar.getInstance();
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        year = calendar.get(Calendar.YEAR);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            //sets formatted date on the button
            @Override
            public void onDateSet(DatePicker datePicker, int y, int m, int d) {
                String formattedDate = String.format("%d-%02d-%02d", y, m+1, d);
                dateFilled.setText(formattedDate);
            }
        }, year, month, day);
        datePickerDialog.show();
    }
    //for showing time so user could select a time
    public void showTime(View view){
        final Button timeFilled = (Button) view;
        Calendar calendar = Calendar.getInstance();
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {

            //sets formatted time on the button
            @Override
            public void onTimeSet(TimePicker timePicker, int h, int m) {
                String formattedTime = String.format("%02d:%02d", h, m);
                timeFilled.setText(formattedTime);
            }
        }, hour, minute, false);
        timePickerDialog.show();
    }

    //for setting a photo for the mood
    public void setImage(View view){
        Intent imageIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (imageIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(imageIntent, REQUEST_IMAGE);
        }
    }

    //gets the photo that was taken and let the image be shown in the page

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode==REQUEST_IMAGE && resultCode==RESULT_OK){
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            view_photo.setImageBitmap(imageBitmap);
        }
        else {

        }
    }


}