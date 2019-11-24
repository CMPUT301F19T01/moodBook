package com.example.moodbook.ui.myMoodMap;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.annotation.NonNull;

import java.util.ArrayList;

/**
 * MyMoodMapFragment.java
 *
 * @author Neilzon Viloria
 * @since 07-11-2019

 *  This activity is used to view a where a users moods take place on a map
 */
public class MyMoodMapFragment extends MoodMapFragment implements OnMapReadyCallback {

    ///// Member Variables /////
    private MapView mapView; // view object
    private GoogleMap moodMap; // map object
    private ArrayList<Mood> moodDataList; // list of moods
    private FirebaseFirestore db; // database
    private FirebaseAuth mAuth; // auth
    private String userID; // users id
    private DBMoodSetter dbMoodSetter;

    /**
     * Required empty public constructor
     */
    public MyMoodMapFragment() {
        // Required empty public constructor
    }


    /**
     * This method is inherited by Fragment
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
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_mymoodmap, container, false);
        //MyMoodMapViewModel = ViewModelProviders.of(this).get(com.example.moodbook.ui.myMoodMap.MyMoodMapViewModel.class);

        mapView = root.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        mapView.getMapAsync(this);

        mapView.onResume();

        // connect to db
        db = FirebaseFirestore.getInstance();

        // create moodDataList
        moodDataList = new ArrayList<>();


        // get current user
        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getUid();

        dbMoodSetter = new DBMoodSetter(mAuth, getContext());

        return root;
    }

    /**
     * This method is inherited by OnMapReadyCallback
     * @param googleMap
     *  internal representation of the map itself
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        // initialize map
        moodMap = googleMap;

        // update list of markers
        updateList(db);

        // for testing purposes
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
    * This method is inherited by OnMapReadyCallback
     */
    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    /**
     * This method is inherited by OnMapReadyCallback
     */
    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    /**
     * This method is inherited by OnMapReadyCallback
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    /**
     * This method is inherited by OnMapReadyCallback
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
    protected void drawMoodMarkers(ArrayList<Mood> moodDataList, GoogleMap moodMap){
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
            moodMap.addMarker(new MarkerOptions().position(moodLatLng).icon(bitmapDescriptor)).setTag(i);

            // zoom in and focus on the most recent mood, ie. the last mood in list
            if(i+1 == moodDataList.size()){
                moodMap.animateCamera(CameraUpdateFactory.newLatLngZoom(moodLatLng, 11.0f));
            }

        }

    }


    /**
     * Queries FireBase for the all the Current users moods
     * @param db
     *  reference to the FireBaseFireStore instance
     * @see MoodMapFragment
     */
    public void updateList(FirebaseFirestore db) {
        try {
            db.collection("USERS")
                    .document(userID)
                    .collection("MOODS")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {

                                // iterate over each document and get fields for drawing mood markers
                                task.getResult();
                                for (int i = 0; i <  task.getResult().size(); i++) {
                                    DocumentSnapshot doc = task.getResult().getDocuments().get(i);
                                    if (doc.exists() && doc.getDouble("location_lat") != null
                                            && doc.getDouble("location_lon") != null) {
                                        try {
                                            Mood mood = DBMoodSetter.getMoodFromData(doc.getData());
                                            mood.setDocId(doc.getId());
                                            moodDataList.add(mood);

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                                drawMoodMarkers(moodDataList, moodMap);
                            }
                        }
                    });
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
