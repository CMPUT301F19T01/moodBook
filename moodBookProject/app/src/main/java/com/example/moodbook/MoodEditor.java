package com.example.moodbook;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.Calendar;

public class MoodEditor {

    // for accessing SelectedMoodState outside of activity
    public interface MoodActivity {
        void setSelectedMoodState(String moodState);
    }


    private static final int REQUEST_IMAGE = 101;


    // Date editor
    // for showing calendar so user could select a date
    public static void showCalendar(final Button view, AppCompatActivity myActivity){
        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int year = calendar.get(Calendar.YEAR);
        DatePickerDialog datePickerDialog = new DatePickerDialog(myActivity,
                new DatePickerDialog.OnDateSetListener() {
            //sets formatted date on the button
            @Override
            public void onDateSet(DatePicker datePicker, int y, int m, int d) {
                String formattedDate = String.format("%d-%02d-%02d", y, m+1, d);
                view.setText(formattedDate);
            }
        }, year, month, day);
        datePickerDialog.show();
    }


    // Time editor
    // for showing time so user could select a time
    public static void showTime(final Button view, AppCompatActivity myActivity){
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(myActivity,
                new TimePickerDialog.OnTimeSetListener() {
            //sets formatted time on the button
            @Override
            public void onTimeSet(TimePicker timePicker, int h, int m) {
                String formattedTime = String.format("%02d:%02d", h, m);
                view.setText(formattedTime);
            }
        }, hour, minute,false);
        timePickerDialog.show();
    }


    // Emotional State editor
    public static void setEmotionSpinner(final AppCompatActivity myActivity, final Spinner spinner_emotion,
                                         MoodStateAdapter emotionAdapter,
                                         final String[] emotionStateList, final int[] emotionColors) {
        spinner_emotion.setAdapter(emotionAdapter);
        spinner_emotion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(myActivity.getApplicationContext(), emotionStateList[i], Toast.LENGTH_LONG)
                        .show();
                ((MoodActivity)myActivity).setSelectedMoodState(emotionStateList[i]);
                if(i != 0) {
                    spinner_emotion.setBackgroundColor(myActivity.getResources().getColor(emotionColors[i]));
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Do nothing
            }
        });
    }


    // Situation editor
    public static ArrayAdapter<String> getSituationAdapter(AppCompatActivity myActivity,
                                                           int spinnerLayoutId,
                                                           String[] situationList) {
        ArrayAdapter<String> situationAdapter = new ArrayAdapter<String>(
                myActivity, spinnerLayoutId, situationList){
            @Override
            public boolean isEnabled(int position){
                return (position != 0);
            }
            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView text = (TextView) view;
                text.setTextColor((position == 0) ? Color.GRAY : Color.BLACK);
                return view;
            }
        };
        situationAdapter.setDropDownViewResource(R.layout.spinner_situation);
        return situationAdapter;
    }

    public static void setSituationSpinner(final AppCompatActivity myActivity, Spinner spinner_situation,
                                           ArrayAdapter<String> situationAdapter) {
        spinner_situation.setAdapter(situationAdapter);
        spinner_situation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItemText = (String) parent.getItemAtPosition(position);
                // first item disabled
                if(position > 0){
                    Toast.makeText(myActivity.getApplicationContext(),
                            "Selected : " + selectedItemText, Toast.LENGTH_SHORT)
                            .show();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Do nothing
            }
        });
    }


    // Image editor
    // for setting a photo for the mood
    public static void setImage(AppCompatActivity myActivity) {
        Intent imageIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (imageIntent.resolveActivity(myActivity.getPackageManager()) != null) {
            myActivity.startActivityForResult(imageIntent, REQUEST_IMAGE);
        }
    }

    // gets the photo that was taken and let the image be shown in the page
    public static void getImageResult(int requestCode, int resultCode, @Nullable Intent data,
                                      ImageView image_view_photo) {
        if (requestCode == REQUEST_IMAGE
                && resultCode == AppCompatActivity.RESULT_OK){
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            image_view_photo.setImageBitmap(imageBitmap);
        }
        else {

        }
    }


    // Location editor
    public static LocationManager getLocationManager(AppCompatActivity myActivity) {
        return (LocationManager) myActivity.getSystemService(Context.LOCATION_SERVICE);
    }

    public static LocationListener getLocationListener(final AppCompatActivity myActivity) {
        return new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                double mood_lat = location.getLatitude();
                double mood_lon = location.getLongitude();
                Toast.makeText(myActivity.getApplicationContext(),
                        mood_lat + "   " + mood_lon, Toast.LENGTH_SHORT)
                        .show();
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {} // not implemented

            @Override
            public void onProviderEnabled(String s) {} // not implemented

            @Override
            public void onProviderDisabled(String s) {} // not implemented
        };
    }

    public static void getLocationResult(AppCompatActivity myActivity, LocationManager locationManager,
                                         LocationListener locationListener) {
        // ask user for permission to get location
        if (ActivityCompat.checkSelfPermission(myActivity,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(myActivity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(myActivity,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
            return;
        } else { // permission granted
            // set criteria for accuracy of location provider
            final Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_FINE);
            criteria.setHorizontalAccuracy(Criteria.ACCURACY_HIGH);

            // set to null because we are required to supply looper but not going to use it
            final Looper looper = null;

            locationManager.requestSingleUpdate(criteria, locationListener, looper);
        }
    }
}
