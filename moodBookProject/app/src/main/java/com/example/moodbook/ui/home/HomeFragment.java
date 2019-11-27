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
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

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
 * @see com.example.moodbook.PageFragment
 * @see RecyclerItemTouchHelper.RecyclerItemTouchHelperListener
 */
public class HomeFragment extends PageFragment
        implements RecyclerItemTouchHelper.RecyclerItemTouchHelperListener, DBCollectionListener {

    // Mood History
    private RecyclerView moodListView;
    private MoodListAdapter moodListAdapter;
    private CoordinatorLayout moodHistoryLayout;
    private TextView hiddenMssg;

    // connect to DB
    private DBMoodSetter moodDB;
    private FirebaseAuth mAuth;
    private static final String TAG = HomeFragment.class.getSimpleName();

    private Mood SelectedMood = null;

    /**
     * This is default Fragment onCreateView() which creates view when fragment is created
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return Return root view inherited from PageFragment
     */
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // get root view from PageFragment
        View root = super.onCreateView(inflater, container, savedInstanceState, R.layout.fragment_home);

        // initialize layout
        moodHistoryLayout = root.findViewById(R.id.mood_history_layout);
        hiddenMssg = (TextView) root.findViewById(R.id.empty);

        // Set up recyclerView and adapter
        moodListView = root.findViewById(R.id.mood_history_listView);
        setupAdapter(new MoodListAdapter.OnItemClickListener() {
            // View the selected mood: when a mood item is clicked, start View activity
            @Override
            public void onItemClick(final Mood item) {
                /*// put attributes of selected mood into editIntent
                AlertDialog.Builder alert = new AlertDialog.Builder(
                        getActivity());
                alert.setMessage("Would you like to view or edit this mood");
                alert.setPositiveButton("Edit", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //do your work here
                        dialog.dismiss();
                        Intent editIntent = new Intent(getActivity(), EditMoodActivity.class);
                        getIntentDataFromMood(editIntent, item);
                        startActivity(editIntent);


                    }
                });
                alert.setNegativeButton("View", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //do your work here
                        dialog.dismiss();
                        Intent viewIntent = new Intent(getActivity(), ViewMoodActivity.class);
                        // put attributes of selected mood into editIntent
                        getIntentDataFromMood(viewIntent, item);
                        // add current class name to disable edit button
                        viewIntent.putExtra("page",HomeFragment.class.getSimpleName());
                        startActivity(viewIntent);


                    }
                });
                alert.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                alert.show();
                 */
                Intent viewIntent = new Intent(getActivity(), ViewMoodActivity.class);
                // put attributes of selected mood into Intent
                getIntentDataFromMood(viewIntent, item);
                // add current class name to allow edit button
                viewIntent.putExtra("page", HomeFragment.class.getSimpleName());
                startActivity(viewIntent);
                SelectedMood = item;
            }
        });

        // initialize DB connector
        mAuth = FirebaseAuth.getInstance();
        moodDB = new DBMoodSetter(mAuth, getContext(), TAG);
        //moodDB.setMoodListListener(moodListAdapter);
        moodDB.setMoodListListener(this);

        // Add a mood: when floating add button is clicked, start add activity
        FloatingActionButton add_mood_button = root.findViewById(R.id.mood_history_add_button);
        add_mood_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addIntent = new Intent(getActivity(), CreateMoodActivity.class);
                Toast.makeText(getContext(), "Add a mood", Toast.LENGTH_LONG).show();
                startActivity(addIntent);
            }
        });

        // adding item touch helper
        // only ItemTouchHelper.LEFT added to detect Right to Left swipe
        // if you want both Right -> Left and Left -> Right
        // add pass ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT as param
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(
                0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(moodListView);

        return root;
    }


    public void onSwiped(final RecyclerView.ViewHolder viewHolder, final int direction, final int position) {
        AlertDialog.Builder alert = new AlertDialog.Builder(
                getActivity());
        alert.setMessage("Are you sure you want to delete this Mood?");
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                //do your work here
                if (viewHolder instanceof MoodListAdapter.MyViewHolder) {
                    // backup of removed item for undo purpose
                    final int deletedIndex = viewHolder.getAdapterPosition();
                    final Mood deletedMood = moodListAdapter.getItem(deletedIndex);

                    // remove the item from recycler view
                    //moodListAdapter.removeItem(deletedIndex);
                    removeMoodFromDB(deletedMood, position);

                    // showing snack bar with Undo option
                    Snackbar snackbar = Snackbar
                            .make(moodHistoryLayout,
                                    deletedMood.toString() + " removed from Mood History!",
                                    Snackbar.LENGTH_LONG);
                    snackbar.setAction("UNDO", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // undo is selected, restore the deleted item
                            //moodListAdapter.restoreItem(deletedItem, deletedIndex);
                            moodDB.addMood(deletedMood);
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
                    Log.e(TAG, "left Swipe");
                } else {
                    Log.e(TAG, "Right Swipe");
                }
                moodListAdapter.notifyItemChanged(position);

                dialogInterface.dismiss();
            }
        });

        alert.show();
    }

    /**
     * This override PageFragment onCreateOptionsMenu() which creates menu options when fragment is created,
     * and set up search action to filter emotional state
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // clean up unwanted menu options
        super.onCreateOptionsMenu(menu, inflater);
        // inflate new search action menu
        if (inflater == null) {
            inflater = getActivity().getMenuInflater();
        }
        inflater.inflate(R.menu.mood_history_emotion_filter, menu);

        // set up search action
        final MenuItem searchItem = menu.findItem(R.id.mood_history_action_search);
        final SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchView.setQueryHint("Search Emotions");
        searchView.setIconifiedByDefault(false);    // expand searchView by default
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

    @Override
    public void beforeGettingList() {
        hiddenMssg.setVisibility(View.INVISIBLE);
        moodListAdapter.clear();
    }

    @Override
    public void onGettingItem(Object item) {
        if (item instanceof Mood) {
            moodListAdapter.addItem((Mood) item);
        }
    }

    @Override
    public void afterGettingList() {
        if (moodListAdapter.getItemCount() == 0){
            hiddenMssg.setVisibility(View.VISIBLE);
        }
    }

    /**
     * This method is for setting up the mood list adapter
     * @param itemClickListener - click listener that defines what happens when adapter item is clicked
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
     * This method takes in the Mood object from the clicked row
     *
     * @param intent
     * @param mood
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