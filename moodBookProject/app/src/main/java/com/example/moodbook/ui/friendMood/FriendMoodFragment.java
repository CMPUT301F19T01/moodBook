package com.example.moodbook.ui.friendMood;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.moodbook.DBFriend;
import com.example.moodbook.DBMoodSetter;
import com.example.moodbook.MoodbookUser;
import com.example.moodbook.ui.home.EditMoodActivity;
import com.example.moodbook.Mood;
import com.example.moodbook.MoodInvalidInputException;
import com.example.moodbook.PageFragment;
import com.example.moodbook.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class FriendMoodFragment extends PageFragment implements DBFriend.FriendRecentMoodListListener{

    // Friend Mood
    private ListView friendMoodListView;
    private FriendMoodListAdapter friendMoodListAdapter;

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
        setupAdapter(new AdapterView.OnItemClickListener() {
            // View the selected friendMood: when a mood item is clicked, start view activity
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // get the selected friendMood
                FriendMood selectedFriendMood = (FriendMood)adapterView.getItemAtPosition(i);
                Toast.makeText(getContext(), "Clicked " + selectedFriendMood.toString(), Toast.LENGTH_LONG).show();
                // TODO: go to ViewMoodActivity
                Intent editIntent = new Intent(getActivity(), EditMoodActivity.class);
                // put attributes of selected mood into editIntent
                getIntentDataFromMood(editIntent, selectedFriendMood.getMood());
                //startActivity(editIntent); // not working
            }
        });

        // initialize DB connector
        mAuth = FirebaseAuth.getInstance();
        friendMoodDB = new DBFriend(mAuth, getContext(), TAG);
        friendMoodDB.setFriendRecentMoodListener(this);

        return root;
    }

    /**
     * This method is for setting up the mood list adapter
     * @param itemClickListener
     */
    private void setupAdapter(AdapterView.OnItemClickListener itemClickListener) {
        friendMoodListAdapter = new FriendMoodListAdapter(getContext(), new ArrayList<FriendMood>());
        friendMoodListView.setAdapter(friendMoodListAdapter);
        friendMoodListView.setOnItemClickListener(itemClickListener);
    }

    /**
     * This method takes in the Mood object from the clicked row
     * @param intent
     * @param mood
     */
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

    @Deprecated
    private void testAdd() {
        String[] username = {"a", "b", "c", "d"};
        for(int i = 0; i < username.length; i++) {
            try {
                Mood mood = new Mood("2019-11-1"+i+" 08:00", Mood.Emotion.getNames()[i%4], username[i],
                        null, null, null);
                MoodbookUser user = new MoodbookUser(username[i], username[i] + "@test.com");
                friendMoodListAdapter.add(new FriendMood(user, mood));
            } catch (MoodInvalidInputException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void beforeGettingFriendMoodList() {
        friendMoodListAdapter.clear();
    }

    @Override
    public void onGettingFriendMood(FriendMood item) {
        friendMoodListAdapter.add(item);
    }
}
