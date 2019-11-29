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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

/**
 * This class is a helper class for the prodf
 */
public class ProfileEditor {

    private static final int REQUEST_IMAGE = 101;
    private static final int GET_IMAGE = 102;
    private static final String TAG = ProfileEditor.class.getSimpleName();
    private static Bitmap imageBitmap;

    public interface ProfilePicInterface {
        void setProfilePic(Bitmap bitImage);
    }

    public interface ProfileListener {
        /**
         * This defines task to be done when getting the user document
         */
        void onGettingUserDoc(DocumentSnapshot document);

        /**
         * This defines task to be done when getting the mood document
         */
        void onGettingMoodDoc(DocumentSnapshot document);
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
     * @param requestCode
     *  This is a result code
     * @param resultCode
     *  This is a result code
     * @param data
     *  This is the data obtained from the Camera/Gallery intent
     * @param image_view_photo
     *  This is an ImageView
     * @param myActivity
     *  The class that calls in this method
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

    /**
     * This method creates temporary file for obtained image.
     * @param inContext
     * @return
     * @throws IOException
     */
    private static File createImageFile(Context inContext) throws IOException {
        File storageDir = inContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                "temp",  /* prefix */
                ".jpeg",         /* suffix */
                storageDir      /* directory */
        );

       return image;
    }


    /**
     * This methods updates the user profile to the database.
     * @param UserID
     * UserID of current user
     * @param email
     * Email of current user
     * @param username
     * Username of current user
     * @param phone
     * Phone of current user
     * @param bio
     * Bio of current user
     */
    public static void updateProfile(String UserID, String email, String username, String phone, String bio){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference collectionReference = db.collection("USERS");
        HashMap<String, Object> data = new HashMap<>();
        data.put("email", email);
        data.put("username", username);
        data.put("phone",phone);
        data.put("bio", bio);

        collectionReference
                .document(UserID)
                .set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "uid stored in db");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "failed:" + e.toString());
                    }
                });
    }

    /**
     * This method gets the profile data from the database.
     * @param uid
     * Uid of currentUser
     * @param profileListener
     */
    public static void getProfileData(String uid, @NonNull final ProfileListener profileListener){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference collectionReference = db.collection("USERS");
        DocumentReference docRef = collectionReference.document(uid);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        profileListener.onGettingUserDoc(document);
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

    }

    /**
     * This method gets the mood data from the database
     * @param uid
     * Uid of user
     * @param recent_moodID
     * Recent mood id of the user
     * @param profileListener
     */
    public static void getMoodData(String uid, String recent_moodID,
                                   @NonNull final ProfileListener profileListener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference collectionReference = db.collection("USERS");
        DocumentReference moodRef = collectionReference.document(uid)
                .collection("MOODS").document(recent_moodID);
        moodRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> t) {
                DocumentSnapshot doc = t.getResult();
                if(doc.exists()){
                    profileListener.onGettingMoodDoc(doc);
                }else {
                    Log.d(TAG, "No such mood document");
                }
            }
        });
    }

}
