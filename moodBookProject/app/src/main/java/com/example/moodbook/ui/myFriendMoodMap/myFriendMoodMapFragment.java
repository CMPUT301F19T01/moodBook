package com.example.moodbook.ui.myFriendMoodMap;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.example.moodbook.DBUpdate;
import com.example.moodbook.Mood;
import com.example.moodbook.PageFragment;
import com.example.moodbook.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class myFriendMoodMapFragment extends PageFragment implements OnMapReadyCallback, DBUpdate {
    //private myFriendMoodMapViewModel MyFriendMoodMapViewModel;

    ///// Member Variables /////
    private MapView mapView;
    private GoogleMap moodMap;
    private ArrayList<Mood> moodDataList;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String userID;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = super.onCreateView(inflater, container, savedInstanceState, R.layout.fragment_friendmoodmap);

        mapView = root.findViewById(R.id.friendMapView);
        mapView.onCreate(savedInstanceState);

        mapView.getMapAsync(this);

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

    @Override
    public void updateList(FirebaseFirestore db) {
        // todo: implement this
    }

    private void drawMoodMarkers(ArrayList<Mood> moodDataList){
        // todo: implement this
    }


}
