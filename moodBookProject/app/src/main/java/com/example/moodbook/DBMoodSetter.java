package com.example.moodbook;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.moodbook.ui.friendMood.FriendMood;
import com.example.moodbook.ui.friendMood.FriendMoodListAdapter;
import com.example.moodbook.ui.home.MoodEditor;
import com.example.moodbook.ui.home.MoodListAdapter;
import com.example.moodbook.ui.myFriends.FriendListAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * This class gets the most current instance of a mood in the Database
 */
public class DBMoodSetter {

    public interface MoodListListener {
        void beforeGettingMoodList();
        void onGettingMood(Mood item);
        void afterGettingMoodList();
    }

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private static FirebaseStorage storage;
    private CollectionReference userReference;
    private CollectionReference intReference;
    private StorageReference photoReference;
    private DocumentReference intRef;
    private Context context;
    private String uid;
    private String TAG;         // optional: for log message
    private Bitmap obtainedImg;
    private MoodListListener listListener;

    /**
     * This is a constructor used by Create and Edit Mood Activity to get the current instance of a mood in the database
     *
     * @param mAuth   This is the FirebaseAuth instance for each logged in user
     * @param context This is a handle to get the data and resources that the app needs while it runs
     * @param TAG     This is an optional string used for printing log messages
     */
    public DBMoodSetter(FirebaseAuth mAuth, Context context, String TAG) {
        this.mAuth = mAuth;
        this.db = FirebaseFirestore.getInstance();
        this.storage = FirebaseStorage.getInstance();
        this.uid = mAuth.getCurrentUser().getUid();
        this.userReference = db.collection("USERS");
        this.context = context;
        this.intReference = db.collection("int");
        this.photoReference = storage.getReferenceFromUrl("gs://moodbook-60da3.appspot.com");
        this.TAG = TAG;
    }

    /**
     * This another instance of the DBMoodSetter Constructor that used to get updated mood data from user's mood collection in the database
     * @param mAuth   This is the FirebaseAuth instance for each logged in user
     * @param context This is a handle to get the data and resources that the app needs while it runs
     *
     */
    public DBMoodSetter(FirebaseAuth mAuth, Context context) {
        this(mAuth, context, DBMoodSetter.class.getSimpleName());
    }

    /**
     * This is a constructor used by Mood History to get updated mood data from user's mood collection in the database
     *
     * @param mAuth               This is the FirebaseAuth instance for each logged in user
     * @param context             This is a handle to get the data and resources that the app needs while it runs
     * @param moodHistoryListener This is a listener from Mood History
     */
    public DBMoodSetter(FirebaseAuth mAuth, Context context, @NonNull EventListener moodHistoryListener) {
        this(mAuth, context);
        userReference.document(uid).collection("MOODS")
                .addSnapshotListener(moodHistoryListener);
    }

    /**
     * @param mAuth               This is the FirebaseAuth instance for each logged in user
     * @param context             This is a handle to get the data and resources that the app needs while it runs
     * @param moodHistoryListener This is a listener from Mood History
     * @param TAG                 This is an optional string used for printing log messages
     */
    public DBMoodSetter(FirebaseAuth mAuth, Context context, @NonNull EventListener moodHistoryListener, String TAG) {
        this(mAuth, context, moodHistoryListener);
        this.TAG = TAG;
    }

    public void setMoodListListener(@NonNull MoodListListener listListener) {
        this.listListener = listListener;
        this.userReference.document(uid).collection("MOODS")
                .addSnapshotListener(getMoodEventListener());
    }

    /**
     * This increments moodCount in the database after a new Mood Object is added
     */
    public void setInt() {
        intRef = intReference.document("count");
        // increment moodCount by 1
        intRef.update("mood_Count", FieldValue.increment(1));
    }

    /**
     * This adds new moods to the database by getting the mood DocID and Calling addMoodAfterDocId
     *
     * @param mood This is a mood Object
     * @see Mood
     * @see #addMoodAfterDocId(String, Mood)
     * @see #setInt()
     */
    public void addMood(final Mood mood) {
        DocumentReference intRef = db.collection("int").document("count");
        intRef
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot != null) {
                            // get mood docId from moodCount
                            Double m = documentSnapshot.getDouble("mood_Count");
                            String moodID = String.valueOf(m) + MainActivity.getUsername(); //increment int + username as a moodID
                            addImg(moodID, mood);
                            // add new mood into db
                            addMoodAfterDocId(moodID, mood);
                            // increment moodCount
                            setInt();
                            //moodID = Integer.valueOf(md.intValue());
                        } else {
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, e.toString());
                    }
                });
    }

    /**
     * This add a Mood object to the database after getting the mood docId
     *
     * @param moodDocID This is the mood docID on the database
     * @param mood      This is a mood Object
     * @see Mood
     * @see #addMood(Mood)
     */
    private void addMoodAfterDocId(final String moodDocID, final Mood mood) {
        Map<String, Object> data = getDataFromMood(mood);
        CollectionReference moodReference = userReference.document(uid).collection("MOODS");
        moodReference.document(moodDocID).set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        showStatusMessage("Added successfully: " + moodDocID);
                        // update recent moodID if adding mood is successful
                        DBFriend.setRecentMoodID(db,uid,moodDocID);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showStatusMessage("Adding failed for " + moodDocID + ": " + e.toString());
                    }
                });
    }

    /**
     * This add the reason image to firebase storage
     * @param mood
     *   This is a mood Object
     *   @see Mood
     */
    private void addImg(String moodID, final Mood mood) {
        String picID = moodID;
        StorageReference photoRef = photoReference.child(picID);
        Bitmap bitImage = mood.getReasonPhoto();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if (bitImage != null) {
            bitImage.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            UploadTask uploadTask = photoRef.putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    showStatusMessage("Failed to add mood photo.");
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    showStatusMessage("Successfully added the mood photo");
                }
            });
        }
    }


    /**
     * This removes a Mood object from the database using its docID
     * @param moodID
     *   This is a String object of mood docID on the database
     */
    public void removeMood (String moodID){
        removeMood(moodID, false, null);
    }

    /**
     * This removes a Mood object from the database using its docID
     * and updates the most recent moodID for user
     * @param moodID
     *   This is a String object of mood docID on the database
     */
    public void removeMood (String moodID, String newRecentMoodID){
        removeMood(moodID, true, newRecentMoodID);
    }

    private void removeMood(final String moodID, final boolean updateRecentMoodID,
                            final String newRecentMoodID) {
        CollectionReference moodReference = userReference.document(uid).collection("MOODS");
        // remove selected city
        moodReference.document(moodID).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        showStatusMessage("Deleted successfully: " + moodID);
                        // update recent moodID if deleted mood was the most recent mood
                        if(updateRecentMoodID){
                            DBFriend.setRecentMoodID(db, uid, newRecentMoodID);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showStatusMessage("Deleting failed for " + moodID + ": " + e.toString());
                    }
                });
    }

    /**
     *
     * @param moodID
     */
    private void removeImgFromDB(final String moodID) {
        StorageReference photoRef = photoReference.child(moodID);
        photoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // File deleted successfully
                Log.d(TAG, "Mood image deleted successfully.");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Uh-oh, an error occurred!
                Log.d(TAG, "Failed to delete mood image.");
            }
        });
    }


    /**
     * This edits  a specific mood in the database given the mood's docID and the parameters to edit
     * @param moodID
     *   This is a String object of mood docID on the database
     * @param data
     *   This is a HashMap containing all the fields that need to be updated in the database and their new values
     */
    public void editMood ( final String moodID, HashMap data){
        CollectionReference moodReference = userReference.document(uid).collection("MOODS");
        updateImg(moodID);
        moodReference.document(moodID).update(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        showStatusMessage("Updated successfully: " + moodID);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showStatusMessage("Updated failed for " + moodID + ": " + e.toString());
                    }
                });
    }

    /**
     * This updates an image object to the FireBase storage
     * Instead of taking an a whole Mood object, it will only have to take in a moodID
     * to update the image.
     * @param moodID
     *  This is the moodID of the mood that a user wants to view/edit the image at.
     * This update the reason image in firebase storage
     */
    private void updateImg (String moodID){
        StorageReference photoRef = photoReference.child(moodID);
        Bitmap bitImage = MoodEditor.getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if (bitImage != null) {
            bitImage.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            UploadTask uploadTask = photoRef.putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
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
            final File localFile = File.createTempFile("Images", "jpeg");
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
     * This is used by MoodHistory to get all mood data in the database from user's mood collection
     * @return
     *  Returns a an EventListener that listens for changes in the mood database
     */
    private EventListener<QuerySnapshot> getMoodEventListener() {
        return new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @NonNull FirebaseFirestoreException e) {
                /*// clear the old list
                moodListAdapter.clear();*/
                listListener.beforeGettingMoodList();
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                    // ignore null item
                    if (doc.getId().equals("null")) continue;
                    // Adding mood from FireStore
                    Mood mood = DBMoodSetter.getMoodFromData(doc.getData());
                    if (mood != null) {
                        mood.setDocId(doc.getId());
                        //moodListAdapter.addItem(mood);
                        listListener.onGettingMood(mood);
                    }
                }
                listListener.afterGettingMoodList();
            }
        };
    }


    /**
     * This is used by a to convert Mood object to HashMap data
     * @param mood
     *   This is a Mood Object
     *   @see Mood
     * @return
     *    Returns a hashmap with mood fields on the database and their corresponding values
     */
    public static Map<String, Object> getDataFromMood (Mood mood){
        MoodLocation location = mood.getLocation();
        Map<String, Object> data = new HashMap<>();
        data.put("date", mood.getDateText());
        data.put("time", mood.getTimeText());
        data.put("emotion", mood.getEmotionText());
        data.put("reason_text", mood.getReasonText());
        data.put("situation", mood.getSituation());
        data.put("location_lat", location == null ? null : location.getLatitude());
        data.put("location_lon", location == null ? null : location.getLongitude());
        data.put("location_address", location.getAddress());
        return data;
    }

    /**
     * This is used to convert HashMap data gotten from the database to a Mood Object
     * @param data
     *   This is a hashmap with mood fields on the database and their corresponding values
     * @return
     *   A mood object with fields and values from the data(the hashmap passed into the function)
     */
    public static Mood getMoodFromData (Map<String,Object> data){

        MoodLocation location = null;
        Object location_lat = data.get("location_lat");
        Object location_lon = data.get("location_lon");
        Object location_address = data.get("location_address");
        if (location_lat != null && location_lon != null) {
            location = new MoodLocation(LocationManager.GPS_PROVIDER);
            location.setLatitude((double) location_lat);
            location.setLongitude((double) location_lon);
            location.setAddress((String)location_address);
        }
        Mood newMood = null;
        try {
            newMood = new Mood((String) data.get("date") + " " + (String) data.get("time"),
                    (String) data.get("emotion"), (String) data.get("reason_text"), null,
                    (String) data.get("situation"), location);
        } catch (MoodInvalidInputException e) {
            e.printStackTrace();
        }
        return newMood;
    }

    /**
     * This is a helper method to show status messages
     * @param message
     *  This is a string that contains the status to be shown
     */
    private void showStatusMessage (String message){
        Log.w(TAG, message);
        if(context != null) {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
        }
    }

}
