package com.example.moodbook;

import androidx.fragment.app.FragmentActivity;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class FriendMoodMapActivity extends FragmentActivity implements OnMapReadyCallback, DBUpdate {

    private GoogleMap moodMap;
    private ArrayList<Mood> moodDataList;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_mood_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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
        moodMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        moodMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        moodMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    @Override
    public void updateList(FirebaseFirestore db) {

    }

    public void drawMoodMarkers(ArrayList<Mood> moodData){
        // iterate through moods
        int emotionRes;
        LatLng emotionLatLng;
        int width = 100;
        int height = 100;
        for(int i = 0; i < moodData.size(); i++){
            // get latlng and image resource from Mood
            emotionRes = moodData.get(i).getEmotionImageResource();
            Location location = moodData.get(i).getLocation();
            emotionLatLng = new LatLng(location.getLatitude(), location.getLongitude());

            // convert and draw to map
            BitmapDrawable bitmapdraw = (BitmapDrawable)getResources().getDrawable(emotionRes);
            Bitmap b = bitmapdraw.getBitmap();
            Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
            BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(smallMarker);
            moodMap.addMarker(new MarkerOptions().position(emotionLatLng).icon(bitmapDescriptor));
        }
    }


}
