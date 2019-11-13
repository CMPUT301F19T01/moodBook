package com.example.moodbook.ui.myMoodMap;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.moodbook.DBMoodSetter;
import com.example.moodbook.DBUpdate;
import com.example.moodbook.Mood;
import com.example.moodbook.R;
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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import java.util.ArrayList;

/**
 * MyMoodMapFragment.java
 *
 * @author Neilzon Viloria
 * @since 07-11-2019

 *  This activity is used to view a where a users moods take place on a map
 * @see DBUpdate
 */
public class MyMoodMapFragment extends Fragment implements OnMapReadyCallback, DBUpdate {
    //private com.example.moodbook.ui.myMoodMap.MyMoodMapViewModel MyMoodMapViewModel;

    ///// Member Variables /////
    private MapView mapView; // view object
    private GoogleMap moodMap; // map object
    private ArrayList<Mood> moodDataList; // list of moods
    private FirebaseFirestore db; // database
    private FirebaseAuth mAuth; // auth
    private String userID; // users id
    private ArrayList<Marker> markerArrayList;


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

        // create markerArrayList
        markerArrayList = new ArrayList<>();

        // get current user
        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getUid();

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
                int pos = (int)marker.getTag();
                Log.i("dsaf", marker.getTag().toString() + "   " + moodDataList.get(pos).getEmotionText());
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
     * Inherited by DBUpdate and is implemented by querying FireBase
     * for the all the Current users moods
     * @param db
     *  reference to the FireBaseFireStore instance
     */
    @Override
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
                                            moodDataList.add(DBMoodSetter.getMoodFromData(doc.getData()));

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                                drawMoodMarkers(moodDataList);
                            }
                        }
                    });
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * This method draws all the Users moods on the map
     * @param moodDataList
     *  ArrayList of the users Mood objects
     */
    private void drawMoodMarkers(ArrayList<Mood> moodDataList){
        int emotionResource;
        LatLng moodLatLng;

        for(int i = 0; i < moodDataList.size(); i++){
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


        }
    }

}
