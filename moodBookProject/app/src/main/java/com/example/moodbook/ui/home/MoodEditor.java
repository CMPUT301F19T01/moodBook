package com.example.moodbook.ui.home;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.moodbook.LocationPickerActivity;
import com.example.moodbook.Mood;
import com.example.moodbook.R;
import com.google.common.collect.ObjectArrays;
import com.google.common.primitives.Ints;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

/**
 * This class contains generalized methods used by CreateMoodActivity and EditMoodActivity
 * @see CreateMoodActivity
 * @see EditMoodActivity
 */
public class MoodEditor {

    /**
     * This interface ensures CreateMoodActivity and EditMoodActivity implement setters to
     * set mood attributes from MoodEditor
     * Setters for emotion, situation, location, reason_photo
     */
    public interface MoodEditorInterface {
        void setMoodEmotion(String emotion);
        void setMoodSituation(String situation);
        void setMoodLocation(Location location);
        void setMoodReasonPhoto(Bitmap bitImage);
    }


    private static final String TAG = MoodEditor.class.getSimpleName();
    private static final int REQUEST_IMAGE = 101;
    private static final int GET_IMAGE = 102;
    private static Bitmap imageBitmap;

    public static final String [] EMOTION_STATE_LIST = ObjectArrays.concat(
            new String[]{"Pick mood state ..."}, Mood.Emotion.getNames(), String.class);
    public static final int[] EMOTION_IMAGE_LIST = Ints.concat(
            new int[]{R.color.transparent}, Mood.Emotion.getImageResources());
    public static final int[] EMOTION_COLOR_LIST = Ints.concat(
            new int[]{R.color.transparent}, Mood.Emotion.getColorResources());
    // Situation spinner options
    public static final String[] SITUATION_LIST = new String[]{
            "Add situation ...",
            "Alone",
            "With one person",
            "With two and more",
            "With a crowd"
    };

    /**
     * A method that acts as a Date editor
     * Used by users to set the current date
     * @param view This is a view for a button
     */
    public static void showDate(final Button view){
        Calendar c = Calendar.getInstance();
        String currentDateString = Mood.DATE_FORMATTER.format(c.getTime());
        view.setText(currentDateString);
    }

    /**
     * A method that acts as a time editor
     * Used by users to set a current time
     * @param view
     *  This is a view for a button
     */
    public static void showTime(final Button view){
        Date d = new Date();
        String currentTimeString = Mood.TIME_FORMATTER.format(d);
        view.setText(currentTimeString);
    }

    /**
     * A method that acts as an emotion state editor
     * Used by users to select their current emotional state
     * @param myActivity This is the class that calls on this method
     * @param emotionSpinner This is the spinner for choosing an emotion state
     * @param emotionAdapter This is the adapter for emotion states
     */
    public static void setEmotionSpinner(final AppCompatActivity myActivity, final Spinner emotionSpinner,
                                         MoodStateAdapter emotionAdapter, String moodEmotion) {
        emotionSpinner.setAdapter(emotionAdapter);
        // initial selection for emotion
        if(moodEmotion != null) {
            emotionSpinner.setSelection(emotionAdapter.getPosition(moodEmotion));
        }
        emotionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int i, long l) {
                // first item disabled
                if(i > 0) {
                    String selectedEmotion = EMOTION_STATE_LIST[i];
                    ((MoodEditorInterface)myActivity).setMoodEmotion(selectedEmotion);
                    emotionSpinner.setBackgroundColor(
                            myActivity.getResources().getColor(EMOTION_COLOR_LIST[i]));
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Do nothing
            }
        });
    }

    /**
     * This return ArrayAdapter for setting up a situation editor
     * Used by users to selects their current situation
     * @param myActivity The class that calls in this method
     * @param spinnerLayoutId The view that contains the situation options
     * @return situationAdapter
     */
    public static ArrayAdapter<String> getSituationAdapter(AppCompatActivity myActivity,
                                                           int spinnerLayoutId) {
        ArrayAdapter<String> situationAdapter = new ArrayAdapter<String>(
                myActivity, spinnerLayoutId, SITUATION_LIST){
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
        situationAdapter.setDropDownViewResource(spinnerLayoutId);
        return situationAdapter;
    }


    /**
     * This is a method that allows users to choose a situation from the adapter
     * @param myActivity The class that calls in this method
     * @param situationSpinner The spinner view
     * @param situationAdapter The situation adapter
     */
    public static void setSituationSpinner(final AppCompatActivity myActivity, Spinner situationSpinner,
                                           ArrayAdapter<String> situationAdapter, String moodSituation) {
        situationSpinner.setAdapter(situationAdapter);
        // initial selection for situation
        if(moodSituation != null) {
            situationSpinner.setSelection(situationAdapter.getPosition(moodSituation));
        }
        situationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int i, long l) {
                // first item disabled
                if(i > 0){
                    String selectedSituation = (String) parent.getItemAtPosition(i);
                    ((MoodEditorInterface)myActivity).setMoodSituation(selectedSituation);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Do nothing
            }
        });
    }

    /**
     * A method that returns a bitmap that was set in the imageView
     * @return imageBitmap
     */
    public static Bitmap getBitmap(){
        return imageBitmap;
    }

    /**
     * This is a method that allows the users to add an image to their mood
     * Involves two options, Camera and Gallery, and will take the users to a new activity to choose/take a picture
     * @param myActivity The class that calls in this method
     */
    public static void setImage(final AppCompatActivity myActivity){
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(myActivity);
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Capture photo from camera",
                "Select photo from gallery"};
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                Intent imageIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                if (imageIntent.resolveActivity(myActivity.getPackageManager()) != null) {
                                    Log.i(TAG, "Camera intent successful");
                                    myActivity.startActivityForResult(imageIntent, REQUEST_IMAGE);
                                }
                                break;
                            case 1:
                                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                                photoPickerIntent.setType("image/*");
                                photoPickerIntent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"image/*"});
                                Log.i(TAG, "Gallery intent successful");
                                myActivity.startActivityForResult(photoPickerIntent, GET_IMAGE);
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }

    /**
     * This is a method that gets the photo that was taken/chosen and let the image be shown on the screen
     * @param requestCode This is a result code
     * @param resultCode This is a result code
     * @param data This is the data obtained from the Camera/Gallery intent
     * @param image_view_photo This is an ImageView
     * @param myActivity The class that calls in this method
     */
    public static void getImageResult(int requestCode, int resultCode, @Nullable Intent data,
                               ImageView image_view_photo, final AppCompatActivity myActivity) {
        if (requestCode == REQUEST_IMAGE
                && resultCode == AppCompatActivity.RESULT_OK){
            if (data != null) {
                Bundle extras = data.getExtras();
                imageBitmap = (Bitmap) extras.get("data");
                if (imageBitmap!= null){
                    ((MoodEditorInterface)myActivity).setMoodReasonPhoto(imageBitmap);
                    image_view_photo.setImageBitmap(imageBitmap); //after getting bitmap, set to imageView
                }
            }
        }
        else if (requestCode == GET_IMAGE && resultCode == AppCompatActivity.RESULT_OK) {
            Uri uri = null;
            if (data != null) {
                uri = data.getData();
                try {
                    ParcelFileDescriptor parcelFileDescriptor =
                            myActivity.getContentResolver().openFileDescriptor(uri, "r");
                    FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
                    imageBitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor);
                    parcelFileDescriptor.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //send to DBMoodSetter
                if (imageBitmap!=null){
                    ((MoodEditorInterface)myActivity).setMoodReasonPhoto(imageBitmap);
                    image_view_photo.setImageBitmap(imageBitmap);
                }
            }
        }
        else {
            //does nothing if fails to deliver data
        }
    }


    /**
     * A method that acts as a location editor
     * Used by users to get their current location
     * @param requestCode
     * @param resultCode
     * @param data
     * @param myActivity
     */
    public static void getLocationResult(int requestCode, int resultCode, @Nullable Intent data,
            final AppCompatActivity myActivity) {
        if(requestCode == LocationPickerActivity.REQUEST_EDIT_LOCATION){
            if(resultCode == LocationPickerActivity.EDIT_LOCATION_OK){
                double lat = data.getDoubleExtra("location_lat", 0);
                double lon = data.getDoubleExtra("location_lon", 0);

                Location location = new Location("");
                location.setLatitude(lat);
                location.setLongitude(lon);
                ((MoodEditorInterface)myActivity).setMoodLocation(location);
            }
        }
    }
}