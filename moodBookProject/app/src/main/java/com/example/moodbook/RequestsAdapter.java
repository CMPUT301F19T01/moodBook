package com.example.moodbook;


import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RequestsAdapter extends RecyclerView.Adapter<RequestsAdapter.RequestHolder> {
    private Context context;
    private final RequestsAdapter.OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Mood item);
    }

    public class RequestHolder extends RecyclerView.ViewHolder {
        private TextView dateText, timeText, emotionText;
        private ImageView emotionImage;
        private RelativeLayout viewBackground, viewForeground;


        public RequestHolder(@NonNull View view) {
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

        public void bind(final Mood item, final MoodListAdapter.OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }

        public RequestsAdapter(Context context, ArrayList<Mood> moodList, RequestsAdapter.OnItemClickListener listener){
            this.moodList = moodList;
            this.context = context;
            this.listener = listener;
            this.moodListFull = new ArrayList<>(moodList);
        }
    }


}
