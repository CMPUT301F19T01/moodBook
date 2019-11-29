package com.example.moodbook;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Activity to allow user to set their mood location manually used by the CreateMoodActivity
 */
public class LocationPickerActivity extends FragmentActivity implements OnMapReadyCallback {
    public static final int REQUEST_EDIT_LOCATION = 1;
    public static final int EDIT_LOCATION_OK = 1;

    private GoogleMap mMap; // Map Object
    private Button confirmButton; // Button
    private LatLng pickedLocation; // coordinates of picked location
    private String locationAddress;
    private LocationManager locationManager;
    private LocationListener locationListener;

    /**
     * This method was inherited from FragmentActivity.
     * Used to set map fragment and button click listeners
     * @param savedInstanceState
     *  This a Bundle that passes the saved instance state of the application
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_picker);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        // set confirm button
        confirmButton = findViewById(R.id.confirmButton);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // send result back
                Intent intent = new Intent();
                intent.putExtra("location_lat", pickedLocation.latitude);
                intent.putExtra("location_lon", pickedLocation.longitude);
                intent.putExtra("location_address", locationAddress);
                setResult(EDIT_LOCATION_OK, intent);
                finish();
            }
        });

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     * @param googleMap
     *  This is a non-null instance of google map object
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // setting custom style of map
        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            getApplicationContext(), R.raw.custom_map_json));

            if (!success) {
                Log.e("Custom Map Parsing", "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e("Resource error", "Can't find style. Error: ", e);
        }

        /*
         * Gets users location
         * create location manager and listener
         */
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                // get current location and draw it on map as initial location
                pickedLocation = new LatLng(location.getLatitude(), location.getLongitude());
                locationAddress = convertToAddress(pickedLocation.latitude, pickedLocation.longitude);
                mMap.addMarker(new MarkerOptions().position(pickedLocation));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pickedLocation, 11.0f));
                Log.i("location", String.valueOf(location.getLatitude()));
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {}

            @Override
            public void onProviderEnabled(String s) {}

            @Override
            public void onProviderDisabled(String s) {}
        };


        // ask user for permission to access their location
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        } else {
            // get the users current location
            try {
                locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, locationListener, null);

            } catch (Exception e){
                e.printStackTrace();
            }
        }

        // functionality for placing marker with on clicks
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(latLng));
                pickedLocation = latLng;
                locationAddress = convertToAddress(latLng.latitude, latLng.longitude);

                // use lat and lon as address string if nothing useful is returned
                if(locationAddress == null){
                    locationAddress = String.format("%.5f", latLng.latitude) + " " + String.format("%.5f", latLng.longitude);
                }
            }
        });
    }

    /**
     * This is a helper method to get address from coordinates
     * @param lat
     *  This is a double latitude coordinate value
     * @param lon
     *   This is a double latitude coordinate value
     * @return
     *  Returns string representing address information
     */
    private String convertToAddress(double lat, double lon){
        // create geocode object
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

        try {
            // get 1 result address string from the coordinates
            List<Address> listAddresses = geocoder.getFromLocation(lat, lon, 1);

            // check if list exists and has anything in it
            if (listAddresses != null && listAddresses.size() >0 ){

                return listAddresses.get(0).getAddressLine(0);

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;

    }

}
