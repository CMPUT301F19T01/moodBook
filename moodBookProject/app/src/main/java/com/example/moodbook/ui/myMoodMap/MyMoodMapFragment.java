package com.example.moodbook.ui.myMoodMap;

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

import com.example.moodbook.DBCollectionListener;
import com.example.moodbook.DBMoodSetter;
import com.example.moodbook.MoodMapFragment;
import com.example.moodbook.Mood;
import com.example.moodbook.R;
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

import androidx.annotation.NonNull;

import java.util.ArrayList;

/**
 * This fragment is used to view a where a users moods take place on a map
 * @see MoodMapFragment
 */
public class MyMoodMapFragment extends MoodMapFragment implements OnMapReadyCallback, DBCollectionListener {

    private MapView mapView;
    private GoogleMap moodMap; // map object
    private ArrayList<Mood> moodDataList; // list of moods
    private DBMoodSetter dbMoodSetter;

    /**
     * Required empty public constructor
     */
    public MyMoodMapFragment() {
        // Required empty public constructor
    }

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
        View root = inflater.inflate(R.layout.fragment_mymoodmap, container, false);

        // prepare map
        mapView = root.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        // create moodDataList
        moodDataList = new ArrayList<>();

        // get instance of Firebase
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        // initialize DBMoodSetter
        dbMoodSetter = new DBMoodSetter(mAuth, getContext());
        dbMoodSetter.setMoodListListener(this);

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

        // for testing purposes
        mapView.setContentDescription("MAP READY");

        // set functionality for viewing moods
        moodMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                // get mood from data list with by using tag
                int pos = (int)marker.getTag();
                Mood mood = moodDataList.get(pos);

                // create dialog popup
                Dialog dialog = new Dialog(getContext());

                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(dialog.getWindow().getAttributes());
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.MATCH_PARENT;
                dialog.show();
                dialog.getWindow().setAttributes(lp);

                // bind mood data to dialog layout
                bindViews(mood, dialog, dbMoodSetter, null).show();

                return false;
            }
        });
    }

    /**
    * must call this method from the parent Activity/Fragment's corresponding method.
     */
    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    /**
     * must call this method from the parent Activity/Fragment's corresponding method.
     */
    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    /**
     * must call this method from the parent Activity/Fragment's corresponding method.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    /**
     * must call this method from the parent Activity/Fragment's corresponding method.
     */
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    /**
     * This method draws all the Users moods on the map
     * @param moodDataList
     *  ArrayList of the users Mood objects
     */
    private void drawMoodMarkers(ArrayList<Mood> moodDataList, GoogleMap moodMap){
        int emotionResource;
        LatLng moodLatLng;

        int i;
        for(i = 0; i < moodDataList.size(); i++){
            Mood mood = moodDataList.get(i);
            // get image resource for the mood marker
            emotionResource = mood.getEmotionImageResource();

            // get location of mood
            Location moodLocation = mood.getLocation();
            moodLatLng = new LatLng(moodLocation.getLatitude(), moodLocation.getLongitude());

            // use png image resource as marker icon
            BitmapDrawable bitmapDrawable = (BitmapDrawable) getResources().getDrawable(emotionResource);
            Bitmap b = bitmapDrawable.getBitmap();
            Bitmap smallMarker = Bitmap.createScaledBitmap(b, 100, 100, false);
            BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(smallMarker);

            // draw on map
            moodMap.addMarker(new MarkerOptions().position(moodLatLng).icon(bitmapDescriptor).anchor(0.5f,0.5f)).setTag(i);

            // zoom in and focus on the most recent mood, ie. the last mood in list
            if(i+1 == moodDataList.size()){
                moodMap.animateCamera(CameraUpdateFactory.newLatLngZoom(moodLatLng, 11.0f));
            }

        }

    }


    /**
     * clear list and map before getting items
     */
    @Override
    public void beforeGettingList() {
        moodDataList.clear();
        moodMap.clear();
    }

    /**
     * Defines what to do with the item on getting it
     * @param item
     *  users mood object
     */
    @Override
    public void onGettingItem(Object item) {
        if(item instanceof Mood){
            Mood mood = (Mood) item;
            if (mood.getLocation() != null){
                moodDataList.add(mood);
            }
        }
    }

    // https://stackoverflow.com/questions/28672883/java-lang-illegalstateexception-fragment-not-attached-to-activity Miroslav Michalec  used to check fragment visibility
    /**
     * draws markers after getting whole list
     */
    @Override
    public void afterGettingList() {
        if (isAdded() && mapView.getVisibility() == View.VISIBLE) {
            drawMoodMarkers(moodDataList, moodMap);
        }
    }

}
