package com.example.moodbook.ui.friendMood;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.moodbook.R;

import java.util.ArrayList;
import java.util.Collections;

/**
 * This Adapter is used by Friend Mood to view friends' most recent moods in ListView.
 * @see FriendMoodFragment
 * @see ArrayAdapter
 */
public class FriendMoodListAdapter extends ArrayAdapter {
    private ArrayList<FriendMood> friendMoods;
    private Context context;

    /**
     * This constructor is complete version with predefined moodList
     * @param context
     *  This is a handler to get the data and resources that the app needs while it runs
     * @param friendMoods
     *  This is the arrayList that stores all the friendMoods to be displayed
     */
    public FriendMoodListAdapter(Context context, ArrayList<FriendMood> friendMoods) {
        super(context, 0, friendMoods);
        this.friendMoods = friendMoods;
        this.context = context;
    }

    /**
     * This constructor is used by Friend Moods to handle list of friendMoods in ListView
     * @param context
     */
    public FriendMoodListAdapter(Context context) {
        this(context, new ArrayList<FriendMood>());
    }

    /**
     * This override ArrayAdapter getView()
     * This method displays username, date, time, emotional state, and shows background colour.
     * @param position
     * @param convertView
     * @param parent
     * @return  View
     */
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.friend_mood_item, parent, false);
        }

        FriendMood friendMood = friendMoods.get(position);

        // get views for username, date, time, and emotion
        TextView usernameText = view.findViewById(R.id.friend_mood_item_username);
        TextView dateText = view.findViewById(R.id.friend_mood_item_date);
        TextView timeText = view.findViewById(R.id.friend_mood_item_time);
        TextView emotionText = view.findViewById(R.id.friend_mood_item_emotion_text);
        ImageView emotionImage = view.findViewById(R.id.friend_mood_item_emotion_image);
        LinearLayout viewForeground = view.findViewById(R.id.friend_mood_item_foreground);

        // show username, date, time, and emotion
        usernameText.setText(friendMood.getUsername());
        dateText.setText(friendMood.getMood().getDateText());
        timeText.setText(friendMood.getMood().getTimeText());
        emotionText.setText(friendMood.getMood().getEmotionText());
        emotionImage.setImageResource(friendMood.getMood().getEmotionImageResource());
        viewForeground.setBackgroundColor(
                ContextCompat.getColor(context, friendMood.getMood().getEmotionColorResource()));

        return view;
    }

    /**
     * This add a friendMood, and sort arrayList of friendMoods by dateTime starting from most recent
     * @param object
     *  This is the new friendMood
     */
    @Override
    public void add(@Nullable Object object) {
        if(object == null || object instanceof FriendMood) {
            FriendMood item = (FriendMood)object;
            friendMoods.add(item);
            Collections.sort(friendMoods, Collections.reverseOrder());
            // notify item added
            notifyDataSetChanged();
        }
    }

    /**
     * This remove all the friendMoods from arrayList
     */
    @Override
    public void clear() {
        friendMoods.clear();
        // notify list is cleared
        notifyDataSetChanged();
    }

    /**
     * This return the number of friendMoods
     * @return
     *  Returns the size of arrayList of all the friendMoods
     */
    @Override
    public int getCount() {
        return friendMoods.size();
    }

    /**
     * This return the friendMood at the specified position
     * @param position
     *  This is the position of a friendMood
     * @return
     *  Returns the friendMood at the given position
     */
    @Nullable
    @Override
    public Object getItem(int position) {
        return (position >= 0 && position < friendMoods.size()) ?
             friendMoods.get(position) : null;
    }
}
