package com.example.moodbook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class MoodMapActivity extends AppCompatActivity implements OnMapReadyCallback, DBUpdate {

    ///// Member Variables /////
    private GoogleMap moodMap;
    private ArrayList<Mood> moodDataList;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mood_map);
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
        // initialize map
        moodMap = googleMap;

        // connect to db
        db = FirebaseFirestore.getInstance();

        // test data
        ArrayList<Mood> moodTESTDATA= new ArrayList<>();

        LatLng loc1 = new LatLng(60.03547, -123.75790);
        LatLng loc2 = new LatLng(24.26711, 125.54427);
        LatLng loc3 = new LatLng(3.28003, 107.63163);
        LatLng loc4 = new LatLng(53.28003, -134.63163);

        Mood moodAfraid = new Mood("Afraid", loc1);
        Mood moodAngry = new Mood("Angry", loc2);
        Mood moodHappy = new Mood("Happy", loc3);
        Mood moodSad = new Mood("Sad", loc4);

        moodTESTDATA.add(moodAfraid);
        moodTESTDATA.add(moodAngry);
        moodTESTDATA.add(moodHappy);
        moodTESTDATA.add(moodSad);

        drawMoodMarkers(moodTESTDATA);

        updateList(db);

    }

    // update users mood list
    @Override
    public void updateList(FirebaseFirestore db) {
        // todo: change document path to users token id
        db.collection("USERS").document("MAP_TEST")
                .collection("MOODS")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.i("uwu", document.getId() + " => " + document.getData());
                                // todo: get the moods and put them in the arrayList
                            }
                        } else {
                            Log.w("uwu", "Error getting documents.", task.getException());
                        }
                    }
                });
    }


    public void drawMoodMarkers(ArrayList<Mood> moodData){
        // iterate through moods
        int emotionResource;
        LatLng emotionLatLng;
        int width = 100;
        int height = 100;
        for(int i = 0; i < moodData.size(); i++){
            // get latlng and image resource from Mood
            emotionResource = moodData.get(i).getEmotionImageResource();
            emotionLatLng = moodData.get(i).getlatLng();

            // convert and draw to map
            BitmapDrawable bitmapdraw = (BitmapDrawable)getResources().getDrawable(emotionResource);
            Bitmap b = bitmapdraw.getBitmap();
            Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
            BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(smallMarker);
            moodMap.addMarker(new MarkerOptions().position(emotionLatLng).icon(bitmapDescriptor));
        }
    }
}
