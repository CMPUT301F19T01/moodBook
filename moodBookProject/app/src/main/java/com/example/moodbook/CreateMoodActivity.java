package com.example.moodbook;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class CreateMoodActivity extends AppCompatActivity {

    Button pick_mood;
    Button add_photo;
    ImageView view_photo;
    private static final int REQUEST_IMAGE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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


        add_photo = findViewById(R.id.pick_mood_photo);
        view_photo = findViewById(R.id.fill_mood_photo);
    }


    public void setImage(View view){
        
    }
}
