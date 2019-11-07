/**
 * Reference:
 * Swipe to delete - https://www.androidhive.info/2017/09/android-recyclerview-swipe-delete-undo-using-itemtouchhelper/
 * Item click to edit - https://antonioleiva.com/recyclerview-listener/
 * Filterable - https://www.youtube.com/watch?v=sJ-Z9G0SDhc
 */
package com.example.moodbook.ui.home;

import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moodbook.CreateMoodActivity;
import com.example.moodbook.DBMoodSetter;
import com.example.moodbook.EditMoodActivity;
import com.example.moodbook.Mood;
import com.example.moodbook.MoodListAdapter;
import com.example.moodbook.PageFragment;
import com.example.moodbook.R;
import com.example.moodbook.RecyclerItemTouchHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

/**
 * This fragment for Mood History allows user to view, add, edit and remove moods.
 * @see Mood
 * @see DBMoodSetter
 * @see MoodListAdapter
 * @see com.example.moodbook.PageFragment
 * @see com.example.moodbook.RecyclerItemTouchHelper.RecyclerItemTouchHelperListener
 */
public class HomeFragment extends PageFragment implements RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {

    //private HomeViewModel homeViewModel;

    // Mood History
    private RecyclerView moodListView;
    private MoodListAdapter moodAdapter;
    private CoordinatorLayout moodHistoryLayout;

    // connect to DB
    private DBMoodSetter moodDB;
    private FirebaseAuth mAuth;
    private static final String TAG = HomeFragment.class.getSimpleName();


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = super.onCreateView(inflater, container, savedInstanceState, R.layout.fragment_home);

        // Set up recyclerView and adapter
        moodHistoryLayout = root.findViewById(R.id.mood_history_layout);
        moodListView = root.findViewById(R.id.mood_history_listView);
        setupAdapter(new MoodListAdapter.OnItemClickListener() {
            // Edit the selected mood: when a mood item is clicked, start edit activity
            @Override
            public void onItemClick(Mood item) {
                Toast.makeText(getContext(), "Clicked " + item.getEmotionText(), Toast.LENGTH_LONG).show();
                Intent editIntent = new Intent(getActivity(), EditMoodActivity.class);
                // put attributes of selected mood into editIntent
                getIntentDataFromMood(editIntent, item);
                startActivity(editIntent);
            }
        });

        // initialize DB connector
        mAuth = FirebaseAuth.getInstance();
        moodDB = new DBMoodSetter(mAuth, getContext(),
                DBMoodSetter.getMoodHistoryListener(moodAdapter), TAG);

        // Add a mood: when floating add button is clicked, start add activity
        FloatingActionButton add_mood_button = root.findViewById(R.id.mood_history_add_button);
        add_mood_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addIntent = new Intent(getActivity(), CreateMoodActivity.class);
                startActivity(addIntent);
            }
        });

        // adding item touch helper
        // only ItemTouchHelper.LEFT added to detect Right to Left swipe
        // if you want both Right -> Left and Left -> Right
        // add pass ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT as param
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT,this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(moodListView);

        return root;
    }

    /**
     * callback when recycler view is swiped
     * item will be removed on swiped
     * undo option will be provided in snackbar to restore the item
     */
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof MoodListAdapter.MyViewHolder) {
            // backup of removed item for undo purpose
            final int deletedIndex = viewHolder.getAdapterPosition();
            final Mood deletedMood = moodAdapter.getItem(deletedIndex);

            // remove the item from recycler view
            //moodAdapter.removeItem(deletedIndex);
            moodDB.removeMood(deletedMood.getDocId());

            // showing snack bar with Undo option
            Snackbar snackbar = Snackbar
                    .make(moodHistoryLayout,
                            deletedMood.toString() + " removed from Mood History!",
                            Snackbar.LENGTH_LONG);
            snackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // undo is selected, restore the deleted item
                    //moodAdapter.restoreItem(deletedItem, deletedIndex);
                    moodDB.addMood(deletedMood);
                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
        }
    }

    /**
     * Set up search action
     * to filter emotional state
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        // inflate new search action menu
        if (inflater == null) {
            inflater = getActivity().getMenuInflater();
        }
        inflater.inflate(R.menu.mood_history_emotion_filter, menu);

        final MenuItem searchItem = menu.findItem(R.id.mood_history_action_search);
        final SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                moodAdapter.getFilter().filter(s);
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
                searchView.setQuery("",false);
                moodAdapter.getFilter().filter("");
                return true;
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void setupAdapter(MoodListAdapter.OnItemClickListener itemClickListener) {
        moodAdapter = new MoodListAdapter(getContext(), new ArrayList<Mood>(), itemClickListener);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        moodListView.setLayoutManager(mLayoutManager);
        moodListView.setItemAnimator(new DefaultItemAnimator());
        moodListView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        moodListView.setAdapter(moodAdapter);
    }

    private void getIntentDataFromMood(@NonNull Intent intent, @NonNull Mood mood) {
        Location location = mood.getLocation();
        intent.putExtra("moodID", mood.getDocId());
        intent.putExtra("date",mood.getDateText());
        intent.putExtra("time",mood.getTimeText());
        intent.putExtra("emotion",mood.getEmotionText());
        intent.putExtra("reason_text",mood.getReasonText());
        intent.putExtra("reason_photo", mood.getReasonPhoto());
        intent.putExtra("situation",mood.getSituation());
        intent.putExtra("location_lat", location==null ? null : ((Double)location.getLatitude()).toString());
        intent.putExtra("location_lon", location==null ? null : ((Double)location.getLongitude()).toString());
    }
}