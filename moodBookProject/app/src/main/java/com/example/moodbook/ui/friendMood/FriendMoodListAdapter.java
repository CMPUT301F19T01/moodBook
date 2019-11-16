package com.example.moodbook.ui.friendMood;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.moodbook.R;

import java.util.ArrayList;

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

        TextView usernameText;
        TextView dateText = view.findViewById(R.id.friend_mood_item_date);
        TextView timeText = view.findViewById(R.id.friend_mood_item_time);
        TextView emotionText = view.findViewById(R.id.friend_mood_item_emotion_text);
        ImageView emotionImage = view.findViewById(R.id.friend_mood_item_emotion_image);

        return view;
    }
}
