package com.example.moodbook;

import android.content.Context;
import android.graphics.Bitmap;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Document;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * This class gets the most current instance of a mood in the Database
 */
public class DBMoodSetter {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private CollectionReference userReference;
    private CollectionReference intReference;
    private StorageReference photoReference;
    private DocumentReference intRef;
    private Context context;
    private String uid;
    private String TAG;         // optional: for log message
    private String moodID;

    // used by CreateMoodActivity / EditMoodActivity

    /**
     * This is a constructor used by the for the Create and Edit Mood Activity to get the current instance of a mood in the databse
     * @param mAuth
     *   This is the FirebaseAuth instance for each logged in user
     * @param context
     *   This is a handle to get the data and resources that the app needs while it runs
     *
     */
    public DBMoodSetter(FirebaseAuth mAuth, Context context){
        this.mAuth = mAuth;
        this.db = FirebaseFirestore.getInstance();
        this.storage = FirebaseStorage.getInstance();
        this.uid = mAuth.getCurrentUser().getUid();
        this.userReference = db.collection("USERS");
        this.context = context;
        this.intReference = db.collection("int");
        this.photoReference = storage.getReferenceFromUrl("gs://moodbook-60da3.appspot.com");
    }


    /**
     * This another instance of the DBMoodSetter Constructor that used to get updated mood data from user's mood collection in the database
     * @param mAuth
     *     This is the FirebaseAuth instance for each logged in user
     * @param context
     *    This is a handle to get the data and resources that the app needs while it runs
     * @param TAG
     *    This is an optional string used for printing log messages
     */

    public DBMoodSetter(FirebaseAuth mAuth, Context context, String TAG){
        this(mAuth, context);
        this.TAG = TAG;
    }

    /**
     * This is a constructor used by Mood History to get updated mood data from user's mood collection in the database
     * @param mAuth
     *  This is the FirebaseAuth instance for each logged in user
     * @param context
     *   This is a handle to get the data and resources that the app needs while it runs
     * @param moodHistoryListener
     *   This is a listener from Mood History
     */
    public DBMoodSetter(FirebaseAuth mAuth, Context context, @NonNull EventListener moodHistoryListener){
        this(mAuth, context);
        userReference.document(uid).collection("MOODS")
                .addSnapshotListener(moodHistoryListener);
    }

    /**
     *
     * @param mAuth
     *  This is the FirebaseAuth instance for each logged in user
     * @param context
     *  This is a handle to get the data and resources that the app needs while it runs
     * @param moodHistoryListener
     *  This is a listener from Mood History
     * @param TAG
     *  This is an optional string used for printing log messages
     *
     */
    public DBMoodSetter(FirebaseAuth mAuth, Context context, @NonNull EventListener moodHistoryListener, String TAG){
        this(mAuth, context, moodHistoryListener);
        this.TAG = TAG;
    }

    /**
     * This increments moodCount in the database after a new Mood Object is added
     */
    public void setInt() {
        intRef = intReference.document("count");
        // increment moodCount by 1
        intRef.update("mood_Count", FieldValue.increment(1));
    }

    // gets from database what int it last used, so it could start counting from there

    /**
     * This adds new moods to the database by getting the mood DocID and Calling addMoodAfterDocId
     * @param mood
     *  This is a mood Object
     *  @see Mood
     * @see #addMoodAfterDocId(String, Mood)
     * @see #setInt()
     *
     */
    public void addMood(final Mood mood) {
        DocumentReference intRef = db.collection("int").document("count");
        intRef
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot!=null){
                            // get mood docId from moodCount
                            Double m = documentSnapshot.getDouble("mood_Count");
                            moodID = String.valueOf(m);
                            addImg(mood);
                            addMoodAfterDocId(moodID, mood); //puts the info to DB
                            String moodDocID = String.valueOf(m);
                            // add new mood into db
                            addMoodAfterDocId(moodDocID, mood);
                            // increment moodCount
                            setInt();
                            //moodID = Integer.valueOf(md.intValue());
                        }
                        else{
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
     * @param moodDocID
     *   This is the mood docID on the database
     * @param mood
     *   This is a mood Object
     *   @see Mood
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
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    showStatusMessage("Adding failed for "+moodDocID+": " + e.toString());
                }
            });
    }


    // function to add the reason image to firebase storage
    public void addImg(final Mood mood) {
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
     * This removes a Mood object from the database using its docID
     * @param moodDocID
     *   This is a String object of mood docID on the database
     *
     */
    public void removeMood(final String moodDocID) {
        CollectionReference moodReference = userReference.document(uid).collection("MOODS");
        // remove selected city
        moodReference.document(moodDocID).delete()
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    showStatusMessage("Deleted successfully: " + moodDocID);
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    showStatusMessage("Deleting failed for "+moodDocID+": " + e.toString());
                }
            });
    }

    /**
     * This edits  a specific mood in the database given the mood's docID and the parameters to edit
     * @param moodDocID
     *   This is a String object of mood docID on the database
     * @param data
     *   This is a HashMap containing all the fields that need to be updated in the database and their new values
     */

    public void editMood(final String moodDocID, HashMap data) {
        CollectionReference moodReference = userReference.document(uid).collection("MOODS");

        moodReference.document(moodDocID).update(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        showStatusMessage("Updated successfully: " + moodDocID);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showStatusMessage("Updated failed for "+moodDocID+": " + e.toString());
                    }
                });
    }

    /**
     * This is used by MoodHistory to get all mood data in the database from user's mood collection
     * @param moodAdapter
     *   This is a MoodList Adapter Object
     *   @see MoodListAdapter
     * @return
     *  Returns a an EventListener that listens for changes in the mood database
     */
    // used by MoodHistory to get all mood data from user's mood collection
    public static EventListener<QuerySnapshot> getMoodHistoryListener(final MoodListAdapter moodAdapter) {
        return new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @NonNull FirebaseFirestoreException e) {
                if(moodAdapter != null) {
                    // clear the old list
                    moodAdapter.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        // ignore null item
                        if (doc.getId() != "null") {
                            // Adding mood from FireStore
                            Mood mood = DBMoodSetter.getMoodFromData(doc.getData());
                            if (mood != null) {
                                mood.setDocId(doc.getId());
                                moodAdapter.addItem(mood);
                            }
                        }
                    }
                }
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
     * @see #getMoodFromData(Map)
     */
    public static Map<String, Object> getDataFromMood(Mood mood) {
        Location location = mood.getLocation();
        Map<String, Object> data = new HashMap<>();
        data.put("date", mood.getDateText());
        data.put("time", mood.getTimeText());
        data.put("emotion", mood.getEmotionText());
        data.put("reason_text", mood.getReasonText());
        data.put("situation", mood.getSituation());
        data.put("location_lat", location == null ? null : location.getLatitude());
        data.put("location_lon", location == null ? null : location.getLongitude());
        return data;
    }


    /**
     * This is used to convert HashMap data gotten from the database to a Mood Object
     * @param data
     *   This is a hashmap with mood fields on the database and their corresponding values
     * @return
     *   A mood object with fields and values from the data(the hashmap passed into the function)
     */
    public static Mood getMoodFromData(Map<String, Object> data) {
        Location location = null;
        Object location_lat = data.get("location_lat");
        Object location_lon = data.get("location_lon");
        if(location_lat != null && location_lon != null) {
            location = new Location(LocationManager.GPS_PROVIDER);
            location.setLatitude((double)location_lat);
            location.setLongitude((double)location_lon);
        }
        Mood newMood = null;
        try {
            newMood = new Mood((String)data.get("date")+" "+(String)data.get("time"),
                    (String)data.get("emotion"), (String)data.get("reason_text"), null,
                    (String)data.get("situation"), location);
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
    private void showStatusMessage(String message) {
        Log.w(TAG, message);
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }
}
