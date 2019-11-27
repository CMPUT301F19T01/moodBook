package com.example.moodbook.ui.myFriendMoodMap;

import android.app.Dialog;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.example.moodbook.DBFriend;
import com.example.moodbook.DBListListener;
import com.example.moodbook.DBMoodSetter;
import com.example.moodbook.MoodMapFragment;
import com.example.moodbook.Mood;
import com.example.moodbook.R;
import com.example.moodbook.ui.friendMood.FriendMood;
import com.example.moodbook.ui.myMoodMap.MyMoodMapFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
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
    private ArrayList<FriendMood> moodDataList;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String userID;
    private DBMoodSetter dbMoodSetter;
    private DBFriend friendMoodDB;

    /**
     * Called to have the fragment instantiate its user interface view
     * @param inflater
     *  Object that can be used to inflate any views in the fragment
     * @param container
     *  If non-null, this is the parent view that the fragment's UI
     *  should be attached to. The fragment should not add the view
     *  itself, but this can be used to generate the LayoutParams
     *  of the view. This value may be null.
     * @param savedInstanceState
     * If non-null, this fragment is being re-constructed from a
     * previous saved state as given here.
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
     * Called when the map is ready to be used.
     * @param googleMap
     *  internal representation of the map itself
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        // initialize map
        moodMap = googleMap;

        // setting custom style of map
        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = moodMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            getContext(), R.raw.custom_map_json));

            if (!success) {
                Log.e("Custom Map Parsing", "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e("Resource error", "Can't find style. Error: ", e);
        }

        // update list of markers
        //updateList(db);

        mapView.setContentDescription("MAP READY");

        moodMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                // get mood from data list with by using tag
                int pos = (int)marker.getTag();
                FriendMood mood = moodDataList.get(pos);

                // create dialog popup
                Dialog dialog = new Dialog(getContext());

                // bind mood data to dialog layout
                bindViews(mood.getMood(), dialog, dbMoodSetter, mood.getUsername()).show();

                return false;
            }
        });


    }

    /**
     * You must call this method from the parent Activity/Fragment's corresponding method.
     */
    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    /**
     * You must call this method from the parent Activity/Fragment's corresponding method.
     */
    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    /**
     * You must call this method from the parent Activity/Fragment's corresponding method.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    /**
     * You must call this method from the parent Activity/Fragment's corresponding method.
     */
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    /**
     * draws moods on map
     * @param mood mood object
     * @param i integer that represents the index of the FriendMood in the data list
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
     * clears map and data list before update on friend moods
     */
    @Override
    public void beforeGettingList() {
        moodDataList.clear();
        moodMap.clear();
    }

    /**
     * adds FriendMood item into data list
     * @param item friend mood
     */
    @Override
    public void onGettingItem(Object item) {
        if(item instanceof FriendMood){
            FriendMood mood = (FriendMood) item;
            if (mood.getMood().getLocation() != null){
                moodDataList.add(mood);
                drawMood(mood.getMood(), moodDataList.size()-1);
            }
        }
    }


    /**
     * draws the markers on the map
     */
    @Override
    public void afterGettingList() {
        if (MyFriendMoodMapFragment.this.isVisible()) {
            Mood mood = moodDataList.get(moodDataList.size() - 1).getMood();
            LatLng moodLatLng = new LatLng(mood.getLocation().getLatitude(), mood.getLocation().getLatitude());
            moodMap.animateCamera(CameraUpdateFactory.newLatLngZoom(moodLatLng, 11.0f));
        }
    }
}
