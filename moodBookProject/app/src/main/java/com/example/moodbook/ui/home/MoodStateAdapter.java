package com.example.moodbook.ui.home;

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

import com.example.moodbook.R;

/**
 * This ArrayAdapter is used by CreateMoodActivity & EditMoodActivity for the spinner of mood emotional states
 */
public class MoodStateAdapter extends ArrayAdapter<String> {
    private Context context;
    private String[] states;
    private int[] images;

    /**
     * This constructor is used by CreateMoodActivity & EditMoodActivity to create ArrayAdapter for mood emotional states.
     * @param context
     * This is a context.
     * @param states
     * This is the states text stored in an array.
     * @param images
     * This is the states emotion image stored in an array.
     */
    public MoodStateAdapter(@NonNull Context context, String[] states, int[] images) {
        super(context, R.layout.spinner_item,states);
        this.context = context;
        this.states = states;
        this.images = images;
    }

    /**
     * This method checks if state is enabled.
     * @param position
     * This is the position of the state in the array list.
     * @return
     * This returns true or false if position is not zero.
     */
    @Override
    public boolean isEnabled(int position) {
        return (position != 0);
    }

    /**
     * This method gets a View that displays the data in the drop down popup at the specified position in the data set.
     * @param position
     * This is the index of the item whose view we want.
     * @param convertView
     * The old view to reuse, if possible. If it is not possible to convert this view to display the correct data, this method can create a new view.
     * @param parent
     * The parent that this view will eventually be attached to.
     * @return row
     * This returns a View corresponding to the data at the specified position.
     */
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

    /**
     * This method gets a View that displays the data at the specified position in the data set.
     * @param position
     * The position of the item within the adapter's data set of the item whose view we want.
     * @param convertView
     * The old view to reuse, if possible. If it is not possible to convert this view to display the correct data, this method can create a new view.
     * @param parent
     * This returns a View corresponding to the data at the specified position.
     * @return row
     * 	A View corresponding to the data at the specified position.
     */
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

    /**
     * This method is used to display error message
     * @param v
     * This is a view for TextView.
     * @param s
     * This is the error message you want to be displayed.
     */
    public void setError(View v, CharSequence s) {
        TextView name = (TextView) v.findViewById(R.id.mood_text);
        name.setError(s);
    }

}



