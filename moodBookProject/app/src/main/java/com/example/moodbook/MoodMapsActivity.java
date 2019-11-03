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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;

public class MoodMapsActivity extends AppCompatActivity implements OnMapReadyCallback, DBUpdate {

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
        // connect to db
        db = FirebaseFirestore.getInstance();
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

        // test data
        moodDataList = new ArrayList<>();


        Location l1 = new Location("");
        l1.setLatitude(60.03547);
        l1.setLongitude(-123.75790);

        Location l2 = new Location("");
        l2.setLatitude(24.26711);
        l2.setLongitude(125.54427);

        Location l3 = new Location("");
        l3.setLatitude(3.28003);
        l3.setLongitude(107.63163);

        Location l4 = new Location("");
        l4.setLatitude(53.28003);
        l4.setLongitude(-134.63163);

    /*
        Mood moodAfraid = null;
        try {
            moodAfraid = new Mood("Afraid", l1);
        } catch (MoodInvalidInputException e) {
            e.printStackTrace();
        }
        Mood moodAngry = null;
        try {
            moodAngry = new Mood("Angry", l2);
        } catch (MoodInvalidInputException e) {
            e.printStackTrace();
        }
        Mood moodHappy = null;
        try {
            moodHappy = new Mood("Happy", l3);
        } catch (MoodInvalidInputException e) {
            e.printStackTrace();
        }
        Mood moodSad = null;
        try {
            moodSad = new Mood("Sad", l4);
        } catch (MoodInvalidInputException e) {
            e.printStackTrace();
        }
*/
        updateList(db);

        drawMoodMarkers(moodDataList);

    }

    // update users mood list
    @Override
    public void updateList(FirebaseFirestore db) {
        // todo: change document path to users token id

        db.collection("USERS")
                .document("dU0J4P52UxbcGUyYq9uJjlLvBE82")
                .collection("MOODS")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        Location tempLoc;
                        Mood m;
                        if (task.isSuccessful()){

                            for (QueryDocumentSnapshot doc : task.getResult()){
                                Log.d("message uwu", doc.getString("emotion") + " " + doc.getDouble("location_lat") + " " + doc.getDouble("location_lon"));
                                tempLoc = new Location("");
                                tempLoc.setLatitude(doc.getDouble("location_lat"));
                                tempLoc.setLongitude(doc.getDouble("location_lon"));
                                try {

                                    Date date = Mood.DATETIME_FORMATTER.parse("2016-01-01 12:12");
                                    Log.i("djfkal", date.toString());
                                    m = new Mood(date.toString(), doc.getString("emotion"), tempLoc);

                                    moodDataList.add(m);
                                } catch (Exception e){
                                    e.printStackTrace();
                                }

                            }
                        }
                    }
                });
    }


    public void drawMoodMarkers(ArrayList<Mood> moodData) {
        // iterate through moods
        int emotionResource;
        LatLng emotionLatLng;
        int width = 100;
        int height = 100;
        for (int i = 0; i < moodData.size(); i++) {
            // get latlng and image resource from Mood
            emotionResource = moodData.get(i).getEmotionImageResource();
            Log.i("image", moodData.get(i).getEmotionImageResource().toString());
            Location location = moodData.get(i).getLocation();
            double l = location.getLatitude();
            Log.i("locafda", "fd" );

            emotionLatLng = new LatLng(location.getLatitude(), location.getLongitude());

            // convert and draw to map
            BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(emotionResource);
            Bitmap b = bitmapdraw.getBitmap();
            Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
            BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(smallMarker);
            moodMap.addMarker(new MarkerOptions().position(emotionLatLng).icon(bitmapDescriptor));
        }
    }

}