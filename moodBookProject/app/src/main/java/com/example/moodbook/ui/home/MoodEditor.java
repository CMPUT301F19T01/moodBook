package com.example.moodbook.ui.home;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.location.Location;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.os.StrictMode;
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
import androidx.core.content.FileProvider;

import com.example.moodbook.LocationPickerActivity;
import com.example.moodbook.Mood;
import com.example.moodbook.MoodLocation;
import com.example.moodbook.R;
import com.google.common.collect.ObjectArrays;
import com.google.common.primitives.Ints;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
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
        void setMoodLocation(MoodLocation location);
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

    /* Situation spinner options */
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
     * @param view
     *  This is a view for a button
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
     * @param myActivity
     *  This is the class that calls on this method
     * @param emotionSpinner
     *  This is the spinner for choosing an emotion state
     * @param emotionAdapter
     *  This is the adapter for emotion states
     */
    public static void setEmotionSpinner(final AppCompatActivity myActivity, final Spinner emotionSpinner,
                                         MoodStateAdapter emotionAdapter, String moodEmotion) {
        // ensure myActivity implements MoodEditorInterface
        if(!(myActivity instanceof MoodEditorInterface)){
            throw new IllegalArgumentException(
                    myActivity.getClass().getSimpleName()+" must implement MoodEditorInterface!");
        }

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
     * @param myActivity
     *  The class that calls in this method
     * @param spinnerLayoutId
     *  The view that contains the situation options
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
     * @param myActivity
     *  The class that calls in this method
     * @param situationSpinner
     *  The spinner view
     * @param situationAdapter
     *  The situation adapter
     */
    public static void setSituationSpinner(final AppCompatActivity myActivity, Spinner situationSpinner,
                                           ArrayAdapter<String> situationAdapter, String moodSituation) {
        // ensure myActivity implements MoodEditorInterface
        if(!(myActivity instanceof MoodEditorInterface)){
            throw new IllegalArgumentException(
                    myActivity.getClass().getSimpleName()+" must implement MoodEditorInterface!");
        }

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
     * @return
     *  imageBitmap
     */
    public static Bitmap getBitmap(){
        return imageBitmap;
    }


    /**
     * This is a method that allows the users to add an image to their mood
     * Involves two options, Camera and Gallery, and will take the users to a new activity to choose/take a picture
     * @param myActivity
     *  The class that calls in this method
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
//                                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
//                                StrictMode.setVmPolicy(builder.build());
                                Intent imageIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                File dir = myActivity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                            //    File file = new File(Environment.getExternalStorageDirectory(), "MyPhoto.jpg");
                                Uri uri = FileProvider.getUriForFile(myActivity,
                                        myActivity.getApplicationContext().getPackageName() + ".provider",
                                        new File(dir, "MyPhoto.img"));
                                imageIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, uri);
                                imageIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
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
     * Method that checks dimension, decodes bitmap, and decides on what rotation it should display on the imageView
     * Attemps flexibility on the code, especially when taking a picture with different phones that takes in different orientation
     * @param context
     *
     * @param selectedImage
     *  resource identifier to image
     * @return Bitmap image
     * Reference: https://stackoverflow.com/questions/14066038/why-does-an-image-captured-using-camera-intent-gets-rotated-on-some-devices-on-a
     * @throws IOException
     */
    public static Bitmap getRotatedBitmap(Context context, Uri selectedImage) throws IOException {
        int MAX_HEIGHT = 1024;
        int MAX_WIDTH = 1024;

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        InputStream imageStream = context.getContentResolver().openInputStream(selectedImage);
        BitmapFactory.decodeStream(imageStream, null, options);
        imageStream.close();

        options.inSampleSize = calculateInSampleSize(options, MAX_WIDTH, MAX_HEIGHT); // Calculate inSampleSize

        options.inJustDecodeBounds = false; // decode bitmap
        imageStream = context.getContentResolver().openInputStream(selectedImage);
        Bitmap image = BitmapFactory.decodeStream(imageStream, null, options);

        image = rotateImageIfRequired(context, image, selectedImage);
        return image;
    }


    /**
     * Calculates inSampleSize
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return int inSampleSize
     * Reference: https://stackoverflow.com/questions/14066038/why-does-an-image-captured-using-camera-intent-gets-rotated-on-some-devices-on-a
     */
    private static int calculateInSampleSize(BitmapFactory.Options options,
                                             int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;

            final float totalPixels = width * height;

            // Anything more than 2x the requested pixels has to be sampled down further
            final float totalReqPixelsCap = reqWidth * reqHeight * 2;

            while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
                inSampleSize++;
            }
        }
        return inSampleSize;
    }

    /**
     * Method that rotates image to what is more appropriate to be shown on the page
     * Avoid having to show a vertical oriented image as a horizontal one
     * @param context
     * @param img
     * @param selectedImage
     * @return Bitmap image
     * @throws IOException
     * Reference: https://stackoverflow.com/questions/14066038/why-does-an-image-captured-using-camera-intent-gets-rotated-on-some-devices-on-a
     */
    private static Bitmap rotateImageIfRequired(Context context, Bitmap img, Uri selectedImage) throws IOException {

        InputStream input = context.getContentResolver().openInputStream(selectedImage);
        ExifInterface ei;
        if (Build.VERSION.SDK_INT > 23)
            ei = new ExifInterface(input);
        else
            ei = new ExifInterface(selectedImage.getPath());

        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotateImage(img, 90);
            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotateImage(img, 180);
            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotateImage(img, 270);
            default:
                return img;
        }
    }

    /**
     * Method that actually rotates the image
     * @param img
     * @param degree
     * @return Bitmap rotatedImage
     * Reference: https://stackoverflow.com/questions/14066038/why-does-an-image-captured-using-camera-intent-gets-rotated-on-some-devices-on-a
     */
    private static Bitmap rotateImage(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImage = Bitmap.createBitmap(img, 0, 0,
                img.getWidth(), img.getHeight(), matrix, true);
        img.recycle();
        return rotatedImage;
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
                               ImageView image_view_photo, final AppCompatActivity myActivity) throws IOException {
        if (requestCode == REQUEST_IMAGE && resultCode == AppCompatActivity.RESULT_OK){
            Log.i(TAG, "result code successful");
            //File object of camera image
            File dir = myActivity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            //    File file = new File(Environment.getExternalStorageDirectory(), "MyPhoto.jpg");
            Uri photoUri = FileProvider.getUriForFile(myActivity,
                    myActivity.getApplicationContext().getPackageName() + ".provider",
                    new File(dir, "MyPhoto.img"));
            if (photoUri != null) {

                imageBitmap = getRotatedBitmap(myActivity, photoUri);
                if (imageBitmap != null) {
                    ((MoodEditorInterface) myActivity).setMoodReasonPhoto(imageBitmap);
                    image_view_photo.setImageBitmap(imageBitmap);
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
     *  the integer request code originally supplied to startActivityForResult(), allowing you to identify who this result came from.
     * @param resultCode
     *  The integer result code returned by the child activity through its setResult().
     * @param data
     *  An Intent, which can return result data to the caller
     * @param myActivity
     *
     *
     */
    public static void getLocationResult(int requestCode, int resultCode, @Nullable Intent data,
            final AppCompatActivity myActivity) {
        // ensure myActivity implements MoodEditorInterface
        if(!(myActivity instanceof MoodEditorInterface)){
            throw new IllegalArgumentException(
                    myActivity.getClass().getSimpleName()+" must implement MoodEditorInterface!");
        }

        if(requestCode == LocationPickerActivity.REQUEST_EDIT_LOCATION){
            if(resultCode == LocationPickerActivity.EDIT_LOCATION_OK){
                double lat = data.getDoubleExtra("location_lat", 0);
                double lon = data.getDoubleExtra("location_lon", 0);
                String address = data.getStringExtra("location_address");
                MoodLocation location = new MoodLocation("");
                location.setLatitude(lat);
                location.setLongitude(lon);
                location.setAddress(address);
                ((MoodEditorInterface)myActivity).setMoodLocation(location);
            }
        }
    }
}