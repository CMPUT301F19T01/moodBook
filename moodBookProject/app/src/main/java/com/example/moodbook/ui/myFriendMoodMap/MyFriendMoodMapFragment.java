package com.example.moodbook.ui.myFriendMoodMap;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.example.moodbook.DBFriend;
import com.example.moodbook.DBListListener;
import com.example.moodbook.DBMoodSetter;
import com.example.moodbook.MoodMapFragment;
import com.example.moodbook.Mood;
import com.example.moodbook.MoodbookUser;
import com.example.moodbook.R;
import com.example.moodbook.ui.friendMood.FriendMood;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

/**
 * MyFriendMoodMapFragment.java
 *
 * @author Neilzon Viloria
 * @since 07-11-2019

 *  This activity is used to view a where a users friends' moods take place on a map
 */
public class MyFriendMoodMapFragment extends MoodMapFragment implements OnMapReadyCallback, DBListListener {
    //private MyFriendMoodMapViewModel MyFriendMoodMapViewModel;

    ///// Member Variables /////
    private MapView mapView;
    private GoogleMap moodMap;
    private ArrayList<Mood> moodDataList;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String userID;
    private DBMoodSetter dbMoodSetter;
    private DBFriend friendMoodDB;

    /**
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate( R.layout.fragment_friendmoodmap, container, false);

        mapView = root.findViewById(R.id.friendMapView);
        mapView.onCreate(savedInstanceState);

        mapView.getMapAsync(this);

        // connect to db
        db = FirebaseFirestore.getInstance();

        // create moodDataList
        moodDataList = new ArrayList<>();

        // get current user
        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getUid();
        friendMoodDB = new DBFriend(mAuth, getContext(), MyFriendMoodMapFragment.class.getSimpleName());
        friendMoodDB.setFriendRecentMoodListener(this);

        dbMoodSetter = new DBMoodSetter(mAuth, getContext());

        return root;
    }

    /**
     *
     * @param googleMap
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        // initialize map
        moodMap = googleMap;

        mapView.setContentDescription("MAP READY");

        moodMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                // get mood from data list with by using tag
                int pos = (int)marker.getTag();
                Mood mood = moodDataList.get(pos);

                // create dialog popup
                Dialog dialog = new Dialog(getContext());

                // bind mood data to dialog layout
                bindViews(mood, dialog, dbMoodSetter).show();

                return false;
            }
        });


    }

    /**
     *
     */
    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    /**
     *
     */
    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    /**
     *
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    /**
     *
     */
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    /**
     *
     * @param mood
     * @param i
     */
    private void drawMood(Mood mood, int i ){
        if (mood.getLocation() != null){
            // get image resource for the mood marker
            int emotionResource = mood.getEmotionImageResource();

            // get location of mood
            Location moodLocation = mood.getLocation();
            LatLng moodLatLng = new LatLng(moodLocation.getLatitude(), moodLocation.getLongitude());

            // use png image resource as marker icon
            BitmapDrawable bitmapDrawable = (BitmapDrawable) getResources().getDrawable(emotionResource);
            Bitmap b = bitmapDrawable.getBitmap();
            Bitmap smallMarker = Bitmap.createScaledBitmap(b, 100, 100, false);
            BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(smallMarker);

            // draw on map
            moodMap.addMarker(new MarkerOptions().position(moodLatLng).icon(bitmapDescriptor)).setTag(i);
        }

    }

    /**
     *
     */
    @Override
    public void beforeGettingList() {
        moodDataList.clear();
        moodMap.clear();
    }

    /**
     *
     * @param item
     */
    @Override
    public void onGettingItem(Object item) {
        if(item instanceof FriendMood){
            FriendMood mood = (FriendMood) item;
            if (mood.getMood().getLocation() != null){
                moodDataList.add(mood.getMood());
                drawMood(mood.getMood(), moodDataList.size()-1);
            }

        }
    }


    @Override
    public void afterGettingList() {

    }
}
