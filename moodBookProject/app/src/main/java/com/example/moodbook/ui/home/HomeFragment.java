// Reference:   swipe to delete - https://www.androidhive.info/2017/09/android-recyclerview-swipe-delete-undo-using-itemtouchhelper/
//              item click to edit - https://antonioleiva.com/recyclerview-listener/

package com.example.moodbook.ui.home;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moodbook.CreateMoodActivity;
import com.example.moodbook.EditMoodActivity;
import com.example.moodbook.Mood;
import com.example.moodbook.MoodListAdapter;
import com.example.moodbook.R;
import com.example.moodbook.RecyclerItemTouchHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class HomeFragment extends Fragment implements RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {

    private HomeViewModel homeViewModel;

    private FloatingActionButton add_mood_button;
    private RecyclerView moodListView;
    private MoodListAdapter moodAdapter;
    private CoordinatorLayout moodHistoryLayout;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        // Add a mood: when floating add button is clicked, start add activity
        add_mood_button = root.findViewById(R.id.mood_history_add_button);
        add_mood_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CreateMoodActivity.class);
                startActivity(intent);
            }
        });

        // Set up recyclerView and adapter
        moodHistoryLayout = root.findViewById(R.id.mood_history_layout);
        moodListView = root.findViewById(R.id.mood_history_listView);
        setupAdapter(new MoodListAdapter.OnItemClickListener() {
            // Edit the selected mood: when a mood item is clicked, start edit activity
            @Override
            public void onItemClick(Mood item) {
                Toast.makeText(getContext(), "Clicked " + item.getEmotionText(), Toast.LENGTH_LONG).show();
                Intent editIntent = new Intent(getActivity(), EditMoodActivity.class);
                startActivity(editIntent);
            }
        });
        testAdd();

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
            final Mood deletedItem = moodAdapter.getItem(deletedIndex);

            // get the removed item emotion to display it in snack bar
            String itemEmotion = deletedItem.getEmotionText();

            // remove the item from recycler view
            moodAdapter.removeItem(deletedIndex);

            // showing snack bar with Undo option
            Snackbar snackbar = Snackbar
                    .make(moodHistoryLayout, itemEmotion + " removed from Mood History!", Snackbar.LENGTH_LONG);
            snackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // undo is selected, restore the deleted item
                    moodAdapter.restoreItem(deletedItem, deletedIndex);
                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
        }
    }

    private void setupAdapter(MoodListAdapter.OnItemClickListener itemClickListener) {
        moodAdapter = new MoodListAdapter(getContext(), new ArrayList<Mood>(), itemClickListener);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        moodListView.setLayoutManager(mLayoutManager);
        moodListView.setItemAnimator(new DefaultItemAnimator());
        moodListView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        moodListView.setAdapter(moodAdapter);
    }

    private void testAdd() {
        // test adding
        moodAdapter.addItem(new Mood("2019-09-30 18:00", "sad"));
        moodAdapter.addItem(new Mood("2019-09-01 07:00", "afraid"));
        moodAdapter.addItem(new Mood("2019-09-01 20:00", "angry"));
        moodAdapter.addItem(new Mood(null, "happy"));
    }
}