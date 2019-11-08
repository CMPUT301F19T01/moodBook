package com.example.moodbook;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * This ArrayAdapter is used by CreateMoodActivity & EditMoodActivity for the spinner of mood emotional states
 */
public class MoodStateAdapter extends ArrayAdapter<String> {
    Context context;
    String[] states;
    int[] images;

    /**
     * This constructor is used by CreateMoodActivity & EditMoodActivity to create ArrayAdapter
     * for mood emotional states
     * @param context
     * @param states
     * @param images
     */
    public MoodStateAdapter(@NonNull Context context, String[] states, int[] images) {
        super(context, R.layout.spinner_item,states);
        this.context = context;
        this.states = states;
        this.images = images;
    }

    @Override
    public boolean isEnabled(int position) {
        return (position != 0);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = inflater.inflate(R.layout.spinner_item, null);
            TextView t1 = (TextView) row.findViewById(R.id.mood_text);
            ImageView i1 = (ImageView)row.findViewById(R.id.mood_image);

            t1.setText(states[position]);
            i1.setImageResource(images[position]);
            t1.setTextColor((position == 0) ? Color.GRAY : Color.BLACK);
            return row;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.spinner_item, null);
        TextView t1 = (TextView) row.findViewById(R.id.mood_text);
        ImageView i1 = (ImageView)row.findViewById(R.id.mood_image);

        t1.setText(states[position]);
        i1.setImageResource(images[position]);
        return row;
    }

    public void setError(View v, CharSequence s) {
        TextView name = (TextView) v.findViewById(R.id.mood_text);
        name.setError(s);
    }

}



