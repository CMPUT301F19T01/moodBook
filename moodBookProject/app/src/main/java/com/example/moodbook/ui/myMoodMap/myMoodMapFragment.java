package com.example.moodbook.ui.myMoodMap;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.example.moodbook.DBMoodSetter;
import com.example.moodbook.DBUpdate;
import com.example.moodbook.Mood;
import com.example.moodbook.PageFragment;
import com.example.moodbook.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class myMoodMapFragment extends PageFragment implements OnMapReadyCallback, DBUpdate {
    //private myMoodMapViewModel MyMoodMapViewModel;

    ///// Member Variables /////
    private MapView mapView;
    private GoogleMap moodMap;
    private ArrayList<Mood> moodDataList;
    private FirebaseFirestore db;
    private String userID;

    /*public myMoodMapFragment() {
        // Required empty public constructor
    }*/


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = super.onCreateView(inflater, container, savedInstanceState, R.layout.fragment_mymoodmap);

        mapView = root.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        mapView.getMapAsync(this);

        // connect to db
        db = FirebaseFirestore.getInstance();

        // create moodDataList
        moodDataList = new ArrayList<>();

        // get current user
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getUid();

        return root;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // initialize map
        moodMap = googleMap;

        // update list of markers
        updateList(db);
    }



    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }


    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }


    private void setMoodDataList(ArrayList<Mood> moodDataList){
        this.moodDataList = moodDataList;

    }

    // update users mood list
    @Override
    public void updateList(FirebaseFirestore db) {
        db.collection("USERS")
                .document(userID)
                .collection("MOODS")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Location tempLoc;

                            // iterate over each document and get fields for drawing mood markers
                            for (QueryDocumentSnapshot doc : task.getResult()) {
                                if (doc.exists() && doc.getDouble("location_lat") != null && doc.getDouble("location_lon") != null) {
                                    try {
                                        moodDataList.add(DBMoodSetter.getMoodFromData(doc.getData()));

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                }

                            }
                            // draw markers
                            setMoodDataList(moodDataList);
                            drawMoodMarkers(moodDataList);

                        }
                    }
                });

    }

    private void drawMoodMarkers(ArrayList<Mood> moodDataList){
        int emotionResource;
        LatLng moodLatLng;

        for(Mood mood: moodDataList){
            // get image resource for the mood marker
            emotionResource = mood.getEmotionImageResource();

            Location moodLocation = mood.getLocation();
            moodLatLng = new LatLng(moodLocation.getLatitude(), moodLocation.getLongitude());

            // use png image resource as marker icon
            BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(emotionResource);
            Bitmap b = bitmapdraw.getBitmap();
            Bitmap smallMarker = Bitmap.createScaledBitmap(b, 100, 100, false);
            BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(smallMarker);

            // draw on map
            moodMap.addMarker(new MarkerOptions().position(moodLatLng).icon(bitmapDescriptor));
        }
    }

}
