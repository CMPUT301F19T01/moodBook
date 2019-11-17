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

public class FriendMoodListAdapter extends ArrayAdapter {
    private ArrayList<FriendMood> friendMoods;
    private Context context;

    public FriendMoodListAdapter(Context context, ArrayList<FriendMood> friendMoods) {
        super(context, 0, friendMoods);
        this.friendMoods = friendMoods;
        this.context = context;
    }

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

    @Override
    public void clear() {
        friendMoods.clear();
        // notify list is cleared
        notifyDataSetChanged();
    }
}
