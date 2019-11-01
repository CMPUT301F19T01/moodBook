// Reference:   RecyclerView - https://www.androidhive.info/2017/09/android-recyclerview-swipe-delete-undo-using-itemtouchhelper/
//              Filterable - https://www.youtube.com/watch?v=sJ-Z9G0SDhc

package com.example.moodbook;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;

public class MoodListAdapter extends RecyclerView.Adapter<MoodListAdapter.MyViewHolder> implements Filterable {
    private ArrayList<Mood> moodList;       // contains filtered mood events
    private ArrayList<Mood> moodListFull;   // contains all the mood events
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

    public MoodListAdapter(Context context, ArrayList<Mood> moodList, OnItemClickListener listener){
        this.moodList = moodList;
        this.context = context;
        this.listener = listener;
        this.moodListFull = new ArrayList<>(moodList);
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
        final Mood mood = moodList.get(position);

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
        return moodList.size();
    }

    public Mood getItem(int position) {
        return moodList.get(position);
    }

    // Add a mood item, and sort mood list by dateTime starting from most recent
    public void addItem(Mood item) {
        // add to filtered moodList
        moodList.add(item);
        Collections.sort(moodList, Collections.reverseOrder());
        // add to full moodList
        moodListFull.add(item);
        Collections.sort(moodListFull, Collections.reverseOrder());
        // notify item added
        notifyDataSetChanged();
    }

    // Edit a mood item, and sort mood list by dateTime starting from most recent
    public void editItem(Mood item, int position) {
        // get index of edited item in full moodList
        int posInListFull = moodListFull.indexOf(moodList.get(position));
        // change item in filtered moodList
        moodList.set(position, item);
        Collections.sort(moodList, Collections.reverseOrder());
        // change item in full moodList
        moodListFull.set(posInListFull, item);
        Collections.sort(moodList, Collections.reverseOrder());
        // notify item edited
        notifyItemChanged(position);
    }

    // Remove all mood items
    public void clear() {
        moodList.clear();
        moodListFull.clear();
    }

    @Override
    public Filter getFilter() {
        Filter moodFilter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                ArrayList<Mood> filteredList = new ArrayList<>();
                // if there is no constraint, return all mood events
                if(constraint == null || constraint.length() == 0) {
                    filteredList.addAll(moodListFull);
                }
                // otherwise, find all mood events whose emotional states match constraint
                else {
                    String filterPattern = constraint.toString().toLowerCase().trim();
                    for(Mood item : moodListFull) {
                        if(item.getEmotionText().toLowerCase().startsWith(filterPattern)) {
                            filteredList.add(item);
                        }
                    }
                }
                FilterResults results = new FilterResults();
                results.values = filteredList;
                return results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults results) {
                moodList.clear();
                moodList.addAll((ArrayList)results.values);
                notifyDataSetChanged();
            }
        };
        return moodFilter;
    }


    // Remove a mood item at specified position
    @Deprecated
    public void removeItem(int position) {
        // get index of removed item in full moodList
        int posInListFull = moodListFull.indexOf(moodList.get(position));
        // remove item from filtered moodList
        moodList.remove(position);
        // remove item from filtered moodList
        moodListFull.remove(posInListFull);
        // notify the item removed by position
        // to perform recycler view delete animations
        // NOTE: don't call notifyDataSetChanged()
        notifyItemRemoved(position);
    }

    // Restore a mood item at its original position
    @Deprecated
    public void restoreItem(Mood item, int position) {
        // insert removed item back to filtered moodList
        moodList.add(position, item);
        // add removed item back to full moodList
        moodListFull.add(item);
        Collections.sort(moodListFull, Collections.reverseOrder());
        // notify item added by position
        notifyItemInserted(position);
    }
}
