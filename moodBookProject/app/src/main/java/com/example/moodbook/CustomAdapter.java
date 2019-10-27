/* Depreciated */
package com.example.moodbook;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

@Deprecated
public class CustomAdapter extends ArrayAdapter {
    private ArrayList<Mood> moods;
    private Context context;

    public CustomAdapter(Context context, ArrayList<Mood> moods){
        super(context, 0, moods);
        this.moods = moods;
        this.context = context;
    }

    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.mood_item, parent, false);
        }

        Mood mood = moods.get(position);

        // get TextView for date, time, and emotion, ImageView for emotion
        TextView dateText = view.findViewById(R.id.mood_item_date);
        TextView timeText = view.findViewById(R.id.mood_item_time);
        TextView emotionText = view.findViewById(R.id.mood_item_emotion_text);
        ImageView emotionImage = view.findViewById(R.id.mood_item_emotion_image);

        // show date, time, and emotional state
        dateText.setText(mood.getDateText());
        timeText.setText(mood.getTimeText());
        emotionText.setText(mood.getEmotionText());
        emotionImage.setImageResource(mood.getEmotionImageResource());

        return view;
    }
}
