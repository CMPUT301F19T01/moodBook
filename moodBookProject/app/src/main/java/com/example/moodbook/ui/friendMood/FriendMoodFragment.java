package com.example.moodbook.ui.friendMood;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

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
 * This class shows the list of mood recent mood from the user's friends,
 * showing the username, date, time, and emotion state
 * This fragment for Friend Mood allows user to view moods.
 * @see FriendMood
 * @see DBFriend
 * @see FriendMoodListAdapter
 * @see PageFragment
 * @see DBCollectionListener
 */
public class FriendMoodFragment extends PageFragment implements DBCollectionListener {

    // Friend Mood
    private ListView friendMoodListView;
    private FriendMoodListAdapter friendMoodListAdapter;
    private TextView hiddenMsg;

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
        hiddenMsg = root.findViewById(R.id.friend_mood_empty_msg);

        setupAdapter(new AdapterView.OnItemClickListener() {
            // View the selected friendMood: when a mood item is clicked, start view activity
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // get the selected friendMood
                FriendMood selectedFriendMood = (FriendMood)adapterView.getItemAtPosition(i);
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

        return root;
    }


    /**
     * This is used by DBFriend to perform task before getting the list of moods
     */
    @Override
    public void beforeGettingList() {
        hiddenMsg.setVisibility(View.VISIBLE);         // show empty message
        friendMoodListAdapter.clear();
    }

    /**
     * This is used by DBFriend to perform task when getting a friendMood
     * @param item
     *  This is the new friendMood
     */
    @Override
    public void onGettingItem(Object item) {
        if (hiddenMsg.getVisibility() == View.VISIBLE) {
            hiddenMsg.setVisibility(View.INVISIBLE);   // hide empty message
        }
        if(item instanceof FriendMood) {
            friendMoodListAdapter.add(item);
        }
    }

    /**
     * This should not be used, because list contains friends only, not friend mood.
     */
    @Deprecated
    @Override
    public void afterGettingList() { }


    /**
     * This is for setting up the mood list adapter
     * @param itemClickListener
     */
    private void setupAdapter(AdapterView.OnItemClickListener itemClickListener) {
        friendMoodListAdapter = new FriendMoodListAdapter(getContext());
        friendMoodListView.setAdapter(friendMoodListAdapter);
        friendMoodListView.setOnItemClickListener(itemClickListener);
    }

    /**
     * This takes in the Mood object from the clicked row
     * @param intent
     * @param mood
     * This is a mood Object
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
