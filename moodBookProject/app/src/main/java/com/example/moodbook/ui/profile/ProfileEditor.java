package com.example.moodbook.ui.profile;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class ProfileEditor {

    private static final int REQUEST_IMAGE = 101;
    private static final int GET_IMAGE = 102;
    private static final String TAG = "MyActivity";
    private static Bitmap imageBitmap;

    public interface ProfilePicInterface {
        void setProfilePic(Bitmap bitImage);
    }


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
                                      ImageView image_view_photo, final AppCompatActivity myActivity) throws IOException {
        if (requestCode == REQUEST_IMAGE
                && resultCode == AppCompatActivity.RESULT_OK){
//            Uri uri = null;
               File newfile = createImageFile(myActivity);
            if (data != null) {
                Bundle extras = data.getExtras();
               imageBitmap = (Bitmap) extras.get("data");
//               Bitmap photo = (Bitmap)extras.get("data");
//                Uri uri = getImageUri(myActivity, photo);

                // CALL THIS METHOD TO GET THE ACTUAL PATH

//               Uri uri = data.getData();
//                try {
//                    ParcelFileDescriptor parcelFileDescriptor =
//                            myActivity.getContentResolver().openFileDescriptor(uri, "r");
//                    FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
//                    imageBitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor);
//                    parcelFileDescriptor.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }

                if (imageBitmap!= null){
                    ((ProfileEditor.ProfilePicInterface)myActivity).setProfilePic(imageBitmap);
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
                if (imageBitmap!=null){
                    ((ProfileEditor.ProfilePicInterface)myActivity).setProfilePic(imageBitmap);
                    image_view_photo.setImageBitmap(imageBitmap);
                }
            }
        }
        else {
            //does nothing if fails to deliver data
        }
    }
    public static Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
    private static File createImageFile(Context inContext) throws IOException {
        File storageDir = inContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                "temp",  /* prefix */
                ".jpeg",         /* suffix */
                storageDir      /* directory */
        );

        String imageFilePath = image.getAbsolutePath();
        return image;
    }


}
