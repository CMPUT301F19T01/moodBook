package com.example.moodbook;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

public class CreateMoodActivity extends AppCompatActivity {

    private Button pick_mood, add_photo, add_date, add_time;
    private ImageView view_photo, emoji;
    private TextView mood_state;
    private EditText add_reason;
    private int year, month, day, hour, minute;
    private Spinner situation;
    private static final int REQUEST_IMAGE = 101;
    private String date_mood, time_mood, reason_mood, situation_mood;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_mood);
        final FragmentManager fm = getSupportFragmentManager();
        final SelectMoodStateFragment s = new SelectMoodStateFragment();
        pick_mood = findViewById(R.id.pick_mood_state);
        emoji = findViewById(R.id.show_mood_state);
        add_photo = findViewById(R.id.pick_mood_photo);
        view_photo = findViewById(R.id.fill_mood_photo);
        add_date = findViewById(R.id.pick_mood_date);
        add_time = findViewById(R.id.pick_mood_time);
        situation = (Spinner) findViewById(R.id.pick_mood_situation);
        add_reason = findViewById(R.id.pick_mood_reason);
        final Button add_button = findViewById(R.id.add_mood_button);
        final Button cancel_button = findViewById(R.id.cancel_mood_button);

        pick_mood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                s.show(fm,"Hello");

            }
        });

        //Mood State Displayer
        int e = getIntent().getIntExtra("emoji",0);
        String state = getIntent().getStringExtra("state");
        pick_mood.setText(state);
        emoji.setImageResource(e);

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