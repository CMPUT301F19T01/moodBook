/**
 * Reference:
 * Swipe to delete - https://www.androidhive.info/2017/09/android-recyclerview-swipe-delete-undo-using-itemtouchhelper/
 * Item click to edit - https://antonioleiva.com/recyclerview-listener/
 * Filterable - https://www.youtube.com/watch?v=sJ-Z9G0SDhc
 */
package com.example.moodbook.ui.home;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moodbook.DBCollectionListener;
import com.example.moodbook.DBMoodSetter;
import com.example.moodbook.Mood;
import com.example.moodbook.MoodLocation;
import com.example.moodbook.PageFragment;
import com.example.moodbook.R;
import com.example.moodbook.ViewMoodActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

/**
 * This class shows the mood history of the user, showing the date, time, and emotion state
 * This fragment for Mood History allows user to view, add, edit and remove moods.
 * @see Mood
 * @see DBMoodSetter
 * @see MoodListAdapter
 * @see PageFragment
 * @see RecyclerItemTouchHelper.RecyclerItemTouchHelperListener
 * @see DBCollectionListener
 */
public class HomeFragment extends PageFragment
        implements RecyclerItemTouchHelper.RecyclerItemTouchHelperListener, DBCollectionListener {

    private RecyclerView moodListView;
    private MoodListAdapter moodListAdapter;
    private CoordinatorLayout moodHistoryLayout;
    private TextView hiddenMsg;

    // connect to DB
    private DBMoodSetter moodDB;
    private FirebaseAuth mAuth;
    private static final String TAG = HomeFragment.class.getSimpleName();

    //private Mood SelectedMood = null;

    /**
     * This is default Fragment onCreateView() which creates view when fragment is created
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     *  Return root view inherited from PageFragment
     */
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = super.onCreateView(inflater, container, savedInstanceState, R.layout.fragment_home);

        moodHistoryLayout = root.findViewById(R.id.mood_history_layout);  // initialize layout
        hiddenMsg = root.findViewById(R.id.mood_history_empty_msg); //message to show when there is no mood available

        // Set up recyclerView and adapter
        moodListView = root.findViewById(R.id.mood_history_listView);
        setupAdapter(new MoodListAdapter.OnItemClickListener() {
            // View the selected mood: when a mood item is clicked, start View activity
            @Override
            public void onItemClick(final Mood item) {
                Intent viewIntent = new Intent(getActivity(), ViewMoodActivity.class);
                getIntentDataFromMood(viewIntent, item);
                viewIntent.putExtra("page", HomeFragment.class.getSimpleName()); // add current class name to allow edit button
                startActivity(viewIntent);
                //SelectedMood = item;
            }
        });

        // initialize DB connector
        mAuth = FirebaseAuth.getInstance();
        moodDB = new DBMoodSetter(mAuth, getContext(), TAG);
        moodDB.setMoodListListener(this);

        // Add a mood: when floating add button is clicked, start add activity
        FloatingActionButton add_mood_button = root.findViewById(R.id.mood_history_add_button);
        add_mood_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addIntent = new Intent(getActivity(), CreateMoodActivity.class);
                startActivity(addIntent);
            }
        });

        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(
                0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(moodListView);

        return root;
    }

    /**
     * This method allows a user to swipe a row to be able to delete a mood.
     * @param viewHolder
     * This is the view from RecyclerView
     * @param direction
     * This is the direction of the swipe
     * @param position
     * This is the position of the mood in the list
     */
    public void onSwiped(final RecyclerView.ViewHolder viewHolder, final int direction, final int position) {
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setMessage("Are you sure you want to delete this Mood?");
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (viewHolder instanceof MoodListAdapter.MyViewHolder) {
                    final int deletedIndex = viewHolder.getAdapterPosition(); // backup of removed item for undo purpose
                    final Mood deletedMood = moodListAdapter.getItem(deletedIndex);
                    removeMoodFromDB(deletedMood, position); //removes from DB

                    // showing snack bar with the Undo option
                    Snackbar snackbar = Snackbar
                            .make(moodHistoryLayout,
                                    "Mood " + deletedMood.toString() + " removed from Mood History!",
                                    Snackbar.LENGTH_LONG);

                    snackbar.setAction("UNDO", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            moodDB.addMood(deletedMood); // restores the previously deleted item
                        }
                    });
                    Log.i("testDel", "Deleted mood.");
                    snackbar.setActionTextColor(Color.YELLOW);
                    snackbar.show();
                }
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (direction == ItemTouchHelper.LEFT) {
                    Log.i(TAG, "left Swipe");
                } else {
                    Log.i(TAG, "Right Swipe");
                }
                moodListAdapter.notifyItemChanged(position);
                dialogInterface.dismiss();
            }
        });
        alert.show();
    }

    /**
     * This override PageFragment onCreateOptionsMenu() creates menu options when fragment is created
     * This sets up search action to filter emotional state
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if (inflater == null) {
            inflater = getActivity().getMenuInflater();
        }
        inflater.inflate(R.menu.mood_history_emotion_filter, menu);

        final MenuItem searchItem = menu.findItem(R.id.mood_history_action_search);
        final SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchView.setQueryHint("Search Emotions");
        searchView.setIconifiedByDefault(false);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                moodListAdapter.getFilter().filter(s);
                return false;
            }
        });
        searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem menuItem) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                searchView.setQuery("", false);
                moodListAdapter.getFilter().filter("");
                return true;
            }
        });
    }

    /**
     * This is used by DBMoodSetter to perform task before getting the list of moods
     */
    @Override
    public void beforeGettingList() {
        hiddenMsg.setVisibility(View.INVISIBLE);
        moodListAdapter.clear();
    }

    /**
     * This is used by DBMoodSetter to perform task when getting a mood
     * @param item
     *  This is the new mood
     */
    @Override
    public void onGettingItem(Object item) {
        if (item instanceof Mood) {
            moodListAdapter.addItem((Mood) item);
        }
    }

    /**
     * This is used by DBMoodSetter to perform task after getting the list of moods
     */
    @Override
    public void afterGettingList() {
        if (moodListAdapter.getItemCount() == 0){
            hiddenMsg.setVisibility(View.VISIBLE);
        }
    }


    /**
     * This method is for setting up the mood list adapter
     * @param itemClickListener
     * This is a click listener that defines what happens when adapter item is clicked
     */
    private void setupAdapter(MoodListAdapter.OnItemClickListener itemClickListener) {
        moodListAdapter = new MoodListAdapter(getContext(), itemClickListener);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        moodListView.setLayoutManager(mLayoutManager);
        moodListView.setItemAnimator(new DefaultItemAnimator());
        moodListView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        moodListView.setAdapter(moodListAdapter);
    }

    /**
     * This method takes in the Mood object from the clicked row.
     * @param intent
     * This is an object that provides runtime binding between HomeFragment and another activity.
     * @param mood
     * This is the Mood object obtained.
     */
    public void getIntentDataFromMood(@NonNull Intent intent, @NonNull Mood mood) {
        MoodLocation location = mood.getLocation();
        intent.putExtra("moodID", mood.getDocId());
        intent.putExtra("date", mood.getDateText());
        intent.putExtra("time", mood.getTimeText());
        intent.putExtra("emotion", mood.getEmotionText());
        intent.putExtra("reason_text", mood.getReasonText());
        //intent.putExtra("reason_photo", mood.getReasonPhoto());
        intent.putExtra("situation", mood.getSituation());
        intent.putExtra("location_lat", location==null ? null : location.getLatitudeText());
        intent.putExtra("location_lon", location==null ? null : location.getLongitudeText());
        intent.putExtra("location_address", location == null ? null : location.getAddress());
    }

    /**
     * This method deletes a mood from the database.
     * @param deletedMood
     * This is the Mood obtained that a user would want to delete.
     * @param position
     * This is the position of the mood in the adapter.
     */
    private void removeMoodFromDB(Mood deletedMood, int position) {
        if(position == 0 && moodListAdapter.getItemCount() > 1) {
            Mood newRecentMood = moodListAdapter.getItem(1);
            moodDB.removeMood(deletedMood.getDocId(),newRecentMood.getDocId());
        }
        else {
            moodDB.removeMood(deletedMood.getDocId());
        }
    }
}