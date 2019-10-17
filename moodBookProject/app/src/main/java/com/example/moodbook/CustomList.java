package com.example.moodbook;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class CustomList extends ArrayAdapter {
    private ArrayList<Mood> moods;
    private Context context;

    public CustomList(Context context, ArrayList<Mood> moods){
        super(context, 0, moods);
        this.moods = moods;
        this.context = context;
    }

    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.item, parent, false);
        }

        Mood mood = moods.get(position);

        // get TextView for date, time, and emotional state
//        TextView dateText = view.findViewById(R.id.date_text);
//        TextView timeText = view.findViewById(R.id.time_text);
//        TextView distance = view.findViewById(R.id.distance_text);

        // show date, time, and emotional state
//        dateText.setText(ride.getDate());
//        timeText.setText(ride.getTime());
//        distance.setText(ride.getDistanceText() + " km");

        return view;
    }
}
