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
import android.view.WindowManager;

import androidx.annotation.NonNull;

import com.example.moodbook.DBFriend;
import com.example.moodbook.DBCollectionListener;
import com.example.moodbook.DBMoodSetter;
import com.example.moodbook.MoodMapFragment;
import com.example.moodbook.Mood;
import com.example.moodbook.R;
import com.example.moodbook.ui.friendMood.FriendMood;
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

import java.util.ArrayList;

/**
 * This fragment is used to view a where a users friends' moods take place on a map
 * @see MoodMapFragment
 */
public class MyFriendMoodMapFragment extends MoodMapFragment
        implements OnMapReadyCallback, DBCollectionListener {

    private MapView mapView;
    private GoogleMap moodMap; // googleMap object
    private ArrayList<FriendMood> moodDataList; // data list of friend moods
    private DBMoodSetter dbMoodSetter;
    private FirebaseAuth mAuth;

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
     *  If non-null, this fragment is being re-constructed from a
     *  previous saved state as given here.
     * @return
     *  Return the View for the fragment's UI, or null.
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate( R.layout.fragment_friendmoodmap,
                container, false);

        // prepare map
        mapView = root.findViewById(R.id.friendMapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        // create moodDataList
        moodDataList = new ArrayList<>();

        // gets instance of FireBase
        mAuth = FirebaseAuth.getInstance();

        // setup DBFriend
        DBFriend friendMoodDB = new DBFriend(mAuth, getContext(),
                MyFriendMoodMapFragment.class.getSimpleName());
        friendMoodDB.setFriendRecentMoodListener(this);

        // initialize dbMoodSetter for use
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
        // set map variable
        moodMap = googleMap;

        // setting custom style of map
        try {

            /*
             * Customise the styling of the base map using a
             * JSON object defined in a raw resource file.
             */
            boolean success = moodMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            getContext(), R.raw.custom_map_json));

            if (!success) {
                Log.e("Custom Map Parsing", "Style parsing failed.");
            }

        } catch (Resources.NotFoundException e) {
            Log.e("Resource error", "Can't find style. Error: ", e);
        }

        // for testing purposes
        mapView.setContentDescription("MAP READY");

        // set functionality of viewing moods from clicking on markers
        moodMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                // get mood from data list with by using tag
                int pos = (int)marker.getTag();
                FriendMood friendMood = moodDataList.get(pos);

                // create dialog popup
                Dialog dialog = new Dialog(getContext());

                // set window attributes
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(dialog.getWindow().getAttributes());
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.MATCH_PARENT;
                dialog.show();
                dialog.getWindow().setAttributes(lp);

                // bind mood data to dialog layout
                bindViews(friendMood.getMood(), dialog, dbMoodSetter, friendMood.getUsername()).show();

                return false;
            }
        });

    }

    /**
     * must call this method from the parent Activity/Fragment's corresponding method.
     * Inherited by OnMapReadyCallback
     */
    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    /**
     * must call this method from the parent Activity/Fragment's corresponding method.
     * Inherited by OnMapReadyCallback
     */
    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    /**
     * must call this method from the parent Activity/Fragment's corresponding method.
     * Inherited by OnMapReadyCallback
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    /**
     * must call this method from the parent Activity/Fragment's corresponding method.
     * Inherited by OnMapReadyCallback
     */
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    /**
     * draws a mood on map
     * @param mood
     *  mood object
     * @param i
     *  integer that represents the index of the FriendMood in the data list
     */
    private void drawMood(Mood mood, int i ){
        if (mood.getLocation() != null){

            // get image resource for the mood marker
            int emotionResource = mood.getEmotionImageResource();

            // get location of mood
            Location moodLocation = mood.getLocation();
            LatLng moodLatLng = new LatLng(moodLocation.getLatitude(), moodLocation.getLongitude());

            // use png image resource as marker icon
            BitmapDrawable bitmapDrawable
                    = (BitmapDrawable) getResources().getDrawable(emotionResource);
            Bitmap b = bitmapDrawable.getBitmap();
            Bitmap smallMarker = Bitmap.createScaledBitmap(b, 100, 100, false);
            BitmapDescriptor bitmapDescriptor
                    = BitmapDescriptorFactory.fromBitmap(smallMarker);

            // draw on map
            moodMap.addMarker(new MarkerOptions().position(moodLatLng)
                    .icon(bitmapDescriptor)).setTag(i);

            // zoom in on mood
            moodMap.animateCamera(CameraUpdateFactory.newLatLngZoom(moodLatLng, 11.0f));

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
     * adds FriendMood item into data list and draws marker on map
     * @param item
     *  friend mood
     */
    @Override
    public void onGettingItem(Object item) {
        if(item instanceof FriendMood){
            FriendMood friendMood = (FriendMood) item;
            if (friendMood.getMood().getLocation() != null){
                moodDataList.add(friendMood);
                drawMood(friendMood.getMood(), moodDataList.size()-1);
            }
        }
    }

    /**
     * inherited from DBCollectionListener
     */
    @Override
    public void afterGettingList() { /* do nothing */ }



}
