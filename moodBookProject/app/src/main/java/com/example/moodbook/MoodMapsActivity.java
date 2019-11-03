package com.example.moodbook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class MoodMapsActivity extends AppCompatActivity implements OnMapReadyCallback, DBUpdate {

    ///// Member Variables /////
    private GoogleMap moodMap;
    private ArrayList<Mood> moodDataList;
    private FirebaseFirestore db;
    private String userID;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mood_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // connect to db
        db = FirebaseFirestore.getInstance();

        // create moodDataList
        moodDataList = new ArrayList<>();

        // get current user
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getUid();
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */


    @Override
    public void onMapReady(GoogleMap googleMap) {
        // initialize map
        moodMap = googleMap;

        // update list of markers
        updateList(db);
    }

    // update users mood list
    @Override
    public void updateList(FirebaseFirestore db) {
        // todo: change document path to users token id

        db.collection("USERS")
                .document(userID)
                .collection("MOODS")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()){
                            Location tempLoc;

                            // iterate over each document and get fields for drawing mood markers
                            for (QueryDocumentSnapshot doc : task.getResult()) {
                                if (doc.exists() && doc.getDouble("location_lat") != null && doc.getDouble("location_lon") != null) {

                                    tempLoc = new Location("");
                                    tempLoc.setLatitude(doc.getDouble("location_lat"));
                                    tempLoc.setLongitude(doc.getDouble("location_lon"));

                                    try {

                                        drawMoodMarker(new Mood("2016-01-01 12:12", doc.getString("emotion"), tempLoc));

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                }
                            }
                        }
                    }
                });

    }

    private void drawMoodMarker(Mood mood){
        // get image resource for the mood marker
        int emotionResource = mood.getEmotionImageResource();

        // getting coordinates
        Location moodLocation = mood.getLocation();
        LatLng moodLatLng = new LatLng(moodLocation.getLatitude(), moodLocation.getLongitude());

        // use png image resource as marker icon
        BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(emotionResource);
        Bitmap b = bitmapdraw.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, 100, 100, false);
        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(smallMarker);

        // draw on map
        moodMap.addMarker(new MarkerOptions().position(moodLatLng).icon(bitmapDescriptor));
    }

}