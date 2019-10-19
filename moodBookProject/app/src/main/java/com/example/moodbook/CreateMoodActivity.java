package com.example.moodbook;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

public class CreateMoodActivity extends AppCompatActivity {

    Button pick_mood;
    Button add_photo;
    ImageView view_photo;
    ImageView emoji;
    TextView mood_state;
    Button add_date;
    Button add_time;
    String SelectedMoodState;
    Spinner sp1;
    RelativeLayout pickState;
    MoodStateAdapter adapter;
    String [] states ={"Afraid","Angry","Happy","Sad"};
    int[] images = {R.drawable.afraid, R.drawable.angry,R.drawable.happy,R.drawable.sad};
    int[] colors = {R.color.afraidBrown,R.color.angryRed,R.color.happyYellow,R.color.sadBlue};
    private int year, month, day, hour, minute;
    private static final int REQUEST_IMAGE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_mood);
        final FragmentManager fm = getSupportFragmentManager();
        final SelectMoodStateFragment s = new SelectMoodStateFragment();
        pick_mood = findViewById(R.id.pick_mood_state);
        pick_mood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                s.show(fm,"Hello");

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

        //Mood State Displayer
        int e = getIntent().getIntExtra("emoji",0);
        String state = getIntent().getStringExtra("state");
        mood_state = findViewById(R.id.mood_state);
        mood_state.setText(state);
        emoji = findViewById(R.id.emoji);
        emoji.setImageResource(e);

        add_photo = findViewById(R.id.pick_mood_photo);
        view_photo = findViewById(R.id.fill_mood_photo);
        add_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setImage(view);
            }
        });

        add_date = findViewById(R.id.pick_mood_date);
        add_time = findViewById(R.id.pick_mood_time);
        //handles selecting a calendar
        add_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCalendar(view);
            }
        });
        //handles selecting a time
        add_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTime(view);
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
    }
}
