package com.example.moodbook.ui.friendMood;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.moodbook.DBFriend;
import com.example.moodbook.DBCollectionListener;
import com.example.moodbook.MoodLocation;
import com.example.moodbook.ViewMoodActivity;
import com.example.moodbook.Mood;
import com.example.moodbook.PageFragment;
import com.example.moodbook.R;
import com.google.firebase.auth.FirebaseAuth;

/**
 *
 */
public class FriendMoodFragment extends PageFragment implements DBCollectionListener {

    // Friend Mood
    private ListView friendMoodListView;
    private FriendMoodListAdapter friendMoodListAdapter;
    private TextView hiddenMssg;

    // connect to DB
    private DBFriend friendMoodDB;
    private FirebaseAuth mAuth;
    private static final String TAG = FriendMoodFragment.class.getSimpleName();


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
        // get root view from PageFragment
        View root = super.onCreateView(inflater, container, savedInstanceState, R.layout.fragment_friendmood);

        // Set up listView and adapter
        friendMoodListView = root.findViewById(R.id.friend_mood_listView);
        hiddenMssg = (TextView) root.findViewById(R.id.friend_mood_empty_msg);

        setupAdapter(new AdapterView.OnItemClickListener() {
            // View the selected friendMood: when a mood item is clicked, start view activity
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // get the selected friendMood
                FriendMood selectedFriendMood = (FriendMood)adapterView.getItemAtPosition(i);
                //Toast.makeText(getContext(), "Clicked " + selectedFriendMood.toString(), Toast.LENGTH_LONG).show();
                Intent viewIntent = new Intent(getActivity(), ViewMoodActivity.class);
                // put attributes of selected mood into editIntent
                getIntentDataFromMood(viewIntent, selectedFriendMood.getMood());
                // add current class name to disable edit button
                viewIntent.putExtra("page",FriendMoodFragment.class.getSimpleName());
                viewIntent.putExtra("friend_username", selectedFriendMood.getUsername());
                startActivity(viewIntent);
            }
        });

        // initialize DB connector
        mAuth = FirebaseAuth.getInstance();
        friendMoodDB = new DBFriend(mAuth, getContext(), TAG);
        friendMoodDB.setFriendRecentMoodListener(this);

        if (friendMoodListView.getCount() == 0) {
            hiddenMssg.setVisibility(View.VISIBLE);
        }
        else{
            hiddenMssg.setVisibility(View.INVISIBLE);
        }

        return root;
    }


    /**
     *
     */
    @Override
    public void beforeGettingList() {
        hiddenMssg.setVisibility(View.VISIBLE);         // show empty message
        friendMoodListAdapter.clear();
    }

    /**
     *
     * @param item
     */
    @Override
    public void onGettingItem(Object item) {
        if (hiddenMssg.getVisibility() == View.VISIBLE) {
            hiddenMssg.setVisibility(View.INVISIBLE);   // hide empty message
        }
        if(item instanceof FriendMood) {
            friendMoodListAdapter.add((FriendMood)item);
        }
    }

    /**
     * This method should not be used, because list contains friends only, not friend mood.
     */
    @Deprecated
    @Override
    public void afterGettingList() { }


    /**
     * This method is for setting up the mood list adapter
     * @param itemClickListener
     */
    private void setupAdapter(AdapterView.OnItemClickListener itemClickListener) {
        friendMoodListAdapter = new FriendMoodListAdapter(getContext());
        friendMoodListView.setAdapter(friendMoodListAdapter);
        friendMoodListView.setOnItemClickListener(itemClickListener);
    }

    /**
     * This method takes in the Mood object from the clicked row
     * @param intent
     * @param mood
     */
    private void getIntentDataFromMood(@NonNull Intent intent, @NonNull Mood mood) {
        MoodLocation location = mood.getLocation();
        intent.putExtra("moodID", mood.getDocId());
        intent.putExtra("date",mood.getDateText());
        intent.putExtra("time",mood.getTimeText());
        intent.putExtra("emotion",mood.getEmotionText());
        intent.putExtra("reason_text",mood.getReasonText());
        //intent.putExtra("reason_photo", mood.getReasonPhoto());
        intent.putExtra("situation",mood.getSituation());
        intent.putExtra("location_lat", location==null ? null : location.getLatitudeText());
        intent.putExtra("location_lon", location==null ? null : location.getLongitudeText());
        intent.putExtra("location_address", location == null ? null : location.getAddress());
    }
}
