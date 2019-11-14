package com.example.moodbook.ui.login;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import androidx.annotation.NonNull;


public class ProfilePicSetter {
    private FirebaseFirestore db;
    private static FirebaseStorage storage;
    private StorageReference photoReference;
    private Context context;
    private Bitmap obtainedImg;

    public ProfilePicSetter( Context context) {
        this.db = FirebaseFirestore.getInstance();
        this.storage = FirebaseStorage.getInstance();
        this.context = context;
        this.photoReference = storage.getReferenceFromUrl("gs://moodbook-60da3.appspot.com");
    }

    /**
     * This updates an image object to the FireBase storage
     * Instead of taking an a whole Mood object, it will only have to take in a moodID
     * to update the image.
     * @param moodID
     *  This is the moodID of the mood that a user wants to view/edit the image at.
     * This update the reason image in firebase storage
     * @param moodID
     *   This is the mood docID on the database
     */
    public void updateImg (String moodID){
        StorageReference photoRef = photoReference.child(moodID);
        Bitmap bitImage = ProfileEditor.getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if (bitImage != null) {
            bitImage.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            UploadTask uploadTask = photoRef.putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                    showStatusMessage("Failed to add mood photo.");
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                    showStatusMessage("Successfully added the mood photo");
                }
            });
        }

    }
    /**
     * This gets the image stored from DB
     * Useful when a user wants to view the image that they have previously added for this mood.
     * @param docID
     * @param view
     */
    public void getImageFromDB (String docID,final ImageView view){
        StorageReference ref = photoReference.child(docID);
        try {
            final File localFile = File.createTempFile("Temp", "jpeg");
            ref.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    obtainedImg = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                    view.setImageBitmap(obtainedImg);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * This is a helper method to show status messages
     * @param message
     *  This is a string that contains the status to be shown
     */
    private void showStatusMessage (String message){
        Log.w("Log", message);
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }



}
