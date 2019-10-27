// Reference: https://www.androidhive.info/2017/09/android-recyclerview-swipe-delete-undo-using-itemtouchhelper/

package com.example.moodbook;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MoodListAdapter extends RecyclerView.Adapter<MoodListAdapter.MyViewHolder> {
    private ArrayList<Mood> moods;
    private Context context;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Mood item);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView dateText, timeText, emotionText;
        private ImageView emotionImage;
        private RelativeLayout viewBackground, viewForeground;


        public MyViewHolder(@NonNull View view) {
            super(view);
            dateText  = view.findViewById(R.id.mood_item_date);
            timeText = view.findViewById(R.id.mood_item_time);
            emotionText = view.findViewById(R.id.mood_item_emotion_text);
            emotionImage = view.findViewById(R.id.mood_item_emotion_image);
            viewBackground = view.findViewById(R.id.mood_item_background);
            viewForeground = view.findViewById(R.id.mood_item_foreground);
        }

        public RelativeLayout getViewForeground() {
            return viewForeground;
        }

        public void bind(final Mood item, final OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }
    }

    public MoodListAdapter(Context context, ArrayList<Mood> moods, OnItemClickListener listener){
        this.moods = moods;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.mood_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final Mood mood = moods.get(position);

        // show date, time, and emotional state
        holder.dateText.setText(mood.getDateText());
        holder.timeText.setText(mood.getTimeText());
        holder.emotionText.setText(mood.getEmotionText());
        holder.emotionImage.setImageResource(mood.getEmotionImageResource());
        // show item background color
        holder.viewForeground.setBackgroundColor(
                ContextCompat.getColor(context, mood.getEmotionColorResource()));

        // bind item with onItemClick listener
        holder.bind(mood, listener);
    }

    @Override
    public int getItemCount() {
        return moods.size();
    }

    public Mood getItem(int position) {
        return moods.get(position);
    }

    public void addItem(Mood item) {
        moods.add(item);
        // notify item added
        notifyDataSetChanged();
    }

    public void removeItem(int position) {
        moods.remove(position);
        // notify the item removed by position
        // to perform recycler view delete animations
        // NOTE: don't call notifyDataSetChanged()
        notifyItemRemoved(position);
    }

    public void restoreItem(Mood item, int position) {
        moods.add(position, item);
        // notify item added by position
        notifyItemInserted(position);
    }
}
