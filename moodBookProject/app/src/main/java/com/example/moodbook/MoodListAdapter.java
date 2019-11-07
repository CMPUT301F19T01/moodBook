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

/**
 * This Adapter is used by Mood History and Friend Moods to view and manage moods in RecyclerView.
 * @see com.example.moodbook.ui.home.HomeFragment
 * @see androidx.recyclerview.widget.RecyclerView.Adapter
 * @see android.widget.Filterable
 */
public class MoodListAdapter extends RecyclerView.Adapter<MoodListAdapter.MyViewHolder> implements Filterable {
    private ArrayList<Mood> moodList;       // contains filtered mood events
    private ArrayList<Mood> moodListFull;   // contains all the mood events
    private Context context;
    private final OnItemClickListener listener;

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
     * This constructor is used by Mood History and Friend Moods to handle list of moods in RecyclerView
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
     * and use the mood to set text & images for the fields in the corresponding Recycler item
     * @param holder
     *   This is MyViewHolder that holds fields and layouts for a mood item
     * @param position
     *   This is position of the mood
     */
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

    /**
     * This override RecyclerView.Adapter getItemCount(),
     * and return the number of moods to be displayed
     * @return
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
        // add to filtered moodList
        moodList.add(item);
        Collections.sort(moodList, Collections.reverseOrder());
        // add to full moodList
        moodListFull.add(item);
        Collections.sort(moodListFull, Collections.reverseOrder());
        // notify item added
        notifyDataSetChanged();
    }

    /**
     * This remove all the moods from arrayList and full arrayList
     */
    public void clear() {
        moodList.clear();
        moodListFull.clear();
        // notify list is cleared
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

    /**
     * This remove a mood at specified position
     * Not used in this project
     * @param position
     *  This is the position of a mood to be removed
     */
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

    /**
     * This restore a mood at specified position, and sort full arrayList of moods by dateTime
     * Not used in this project
     * @param item
     *  This is the mood to be restored
     * @param position
     *  This is the position of the mood
     */
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

    /**
     * This update a mood at specified position, and sort arrayList and full arrayList of moods by dateTime
     * Not used in this project
     * @param item
     *  This is the new mood to replace the old one
     * @param position
     *  This is the position of the old mood
     */
    @Deprecated
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
}
