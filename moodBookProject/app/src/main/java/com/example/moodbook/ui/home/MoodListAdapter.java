/**
 * Reference:
 * RecyclerView - https://www.androidhive.info/2017/09/android-recyclerview-swipe-delete-undo-using-itemtouchhelper/
 * Filterable - https://www.youtube.com/watch?v=sJ-Z9G0SDhc
 */
package com.example.moodbook.ui.home;

import android.content.Context;
import android.util.Log;
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

import com.example.moodbook.Mood;
import com.example.moodbook.R;

import java.util.ArrayList;
import java.util.Collections;

/**
 * This Adapter is used by Mood History to view and manage moods in RecyclerView.
 * @see HomeFragment
 * @see androidx.recyclerview.widget.RecyclerView.Adapter
 * @see android.widget.Filterable
 */
public class MoodListAdapter extends RecyclerView.Adapter<MoodListAdapter.MyViewHolder> implements Filterable {
    private ArrayList<Mood> moodList;       // contains filtered mood events
    private ArrayList<Mood> moodListFull;   // contains all the mood events
    private Context context;
    private final OnItemClickListener listener;

    private Filter mood_filter;


    /**
     * This interface requires the fragment that is using MoodListAdapter to define onItemClick event.
     * @see MoodListAdapter
     * @see com.example.moodbook.ui.home.HomeFragment
     */
    public interface OnItemClickListener {
        void onItemClick(Mood item);
    }


    /**
     * This subclass ViewHolder is used by MoodListAdapter to manage fields and layouts in each RecyclerView item
     * as well as to bind each RecyclerView item with an onItemClick event defined by OnItemClickListener.
     * @see MoodListAdapter
     */
    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView dateText, timeText, emotionText;
        private ImageView emotionImage;
        private RelativeLayout viewBackground, viewForeground;

        /**
         * This constructor is used by MoodListAdapter to manage fields and layouts in a RecyclerView item
         * @param view
         *   This is the view that holds all views within the RecyclerView item
         */
        public MyViewHolder(@NonNull View view) {
            super(view);
            dateText  = view.findViewById(R.id.mood_item_date);
            timeText = view.findViewById(R.id.mood_item_time);
            emotionText = view.findViewById(R.id.mood_item_emotion_text);
            emotionImage = view.findViewById(R.id.mood_item_emotion_image);
            viewBackground = view.findViewById(R.id.mood_item_background);
            viewForeground = view.findViewById(R.id.mood_item_foreground);
        }

        /**
         * This return foreground layout
         * @return
         *  Returns RelativeLayout for foreground which is mood item
         */
        public RelativeLayout getViewForeground() {
            return viewForeground;
        }

        /**
         * This bind a mood with onItemClick event
         * @param item
         *  This is the mood to bind
         * @param listener
         *  This is OnItemClickListener which defines what happen after clicking the mood
         */
        public void bind(final Mood item, final OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }
    }


    /**
     * This constructor is complete version with predefined moodList
     * @param context
     *   This is a handler to get the data and resources that the app needs while it runs
     * @param moodList
     *   This is the arrayList that stores all the moods to be displayed
     * @param listener
     *   This is a item click event listener, which will start EditMoodActivity
     */
    public MoodListAdapter(Context context, ArrayList<Mood> moodList, OnItemClickListener listener){
        this.moodList = moodList;
        this.context = context;
        this.listener = listener;
        this.moodListFull = new ArrayList<>(moodList);
        this.mood_filter = getFilter();
    }

    /**
     * This constructor is used by Mood History to handle list of moods in RecyclerView
     * @param context
     * @param listener
     */
    public MoodListAdapter(Context context, OnItemClickListener listener){
        this(context, new ArrayList<Mood>(), listener);
    }

    /**
     * This override RecyclerView.Adapter onCreateViewHolder(),
     * and return MyViewHolder that holds fields and layouts for each mood item in RecyclerView
     * @param parent
     *   This is the parent view that hold all mood item views.
     * @return
     *   This is MyViewHolder that holds fields and layouts for a mood item
     */
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.mood_item, parent, false);
        return new MyViewHolder(itemView);
    }

    /**
     * This override RecyclerView.Adapter onBindViewHolder(),
     * and use the mood to set text & images for the fields in the corresponding Recycler item.
     * This method displays date, time, emotional state, and shows background colour.
     * @param holder
     *   This is MyViewHolder that holds fields and layouts for a mood item
     * @param position
     *   This is position of the mood
     */
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final Mood mood = moodList.get(position);

        holder.dateText.setText(mood.getDateText());
        holder.timeText.setText(mood.getTimeText());
        holder.emotionText.setText(mood.getEmotionText());
        holder.emotionImage.setImageResource(mood.getEmotionImageResource());

        holder.viewForeground.setBackgroundColor(
                ContextCompat.getColor(context, mood.getEmotionColorResource()));

        holder.bind(mood, listener);
    }

    /**
     * This override RecyclerView.Adapter getItemCount(), and return the number of moods to be displayed
     * @return int
     *    Returns the size of arrayList of all the moods that will be displayed
     */
    @Override
    public int getItemCount() {
        return moodList.size();
    }

    /**
     * This return the mood at the specified position
     * @param position
     *   This is the position of a mood
     * @return
     *   Returns the mood at the given position
     */
    public Mood getItem(int position) {
        return moodList.get(position);
    }

    /**
     * This add a mood, and sort arrayList and full arrayList of moods by dateTime starting from most recent
     * @param item
     *   This is the new mood
     */
    public void addItem(Mood item) {
        moodList.add(item); /* add to filtered moodList */
        Collections.sort(moodList, Collections.reverseOrder());
        moodListFull.add(item); /* add to full moodList */
        Collections.sort(moodListFull, Collections.reverseOrder());
        notifyDataSetChanged();
    }

    /**
     * This remove all the moods from arrayList and full arrayList
     */
    public void clear() {
        moodList.clear();
        moodListFull.clear();
        notifyDataSetChanged();
    }

    /**
     * This override Filterable getFilter(),
     * and return the filter for filter out moods that don't have matching emotional state
     * @return
     *  Return the filter that finds moods with matching emotional state
     */
    @Override
    public Filter getFilter() {
        if(this.mood_filter == null) {
            this.mood_filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    ArrayList<Mood> filteredList = new ArrayList<>();
                    if (constraint == null || constraint.length() == 0) {
                        filteredList.addAll(moodListFull); /* if there is no constraint, return all mood events */
                    }
                    else {
                        String filterPattern = constraint.toString().toLowerCase().trim();
                        for (Mood item : moodListFull) {
                            if (item.getEmotionText().toLowerCase().startsWith(filterPattern)) {
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
                    moodList.addAll((ArrayList) results.values);
                    notifyDataSetChanged();
                    Log.d(MoodListAdapter.this.getClass().getSimpleName(),
                            "size:" + MoodListAdapter.this.getItemCount());
                }
            };
        }
        return this.mood_filter;
    }

}
