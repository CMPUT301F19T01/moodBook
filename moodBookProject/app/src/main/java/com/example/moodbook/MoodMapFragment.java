package com.example.moodbook;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

/**
 *
 */
public class MoodMapFragment extends PageFragment{

    /**
     * Binds the mood data to a dialog for viewing on map view
     *
     * @param mood the mood who's data we are binding
     * @param dialog the dialog that contains the views where we want to bind the mood data
     * @param dbMoodSetter uses a "getImageFromDB" method from this object to get image
     * @return the dialog with all the mood data binded to the views
     */
    protected Dialog bindViews(Mood mood, final Dialog dialog, DBMoodSetter dbMoodSetter){
        // set dialog to custom layout
        dialog.setContentView(R.layout.activity_view_mood);

        // show the reason photo
        ImageView reasonPhoto = dialog.findViewById(R.id.view_reason_photo);
        dbMoodSetter.getImageFromDB(mood.getDocId(), reasonPhoto);

        // set mood emotion image
        ImageView imageView = dialog.findViewById(R.id.view_mood_image);
        imageView.setImageResource(mood.getEmotionImageResource());

        // set mood text
        TextView moodText = dialog.findViewById(R.id.view_emotion);
        moodText.setText(mood.getEmotionText());

        // set mood date time
        TextView moodDate = dialog.findViewById(R.id.view_date_time);
        moodDate.setText(mood.getDateText());

        // set mood time
        TextView moodTime = dialog.findViewById(R.id.view_time);
        moodTime.setText(mood.getTimeText());

        // set mood location
        TextView moodLocation = dialog.findViewById(R.id.view_location);
        Location loc = mood.getLocation();
        moodLocation.setText(loc.getLatitude() + " " + loc.getLongitude());

        // set mood situation
        TextView moodSituation = dialog.findViewById(R.id.view_situation);
        moodSituation.setText(mood.getSituation());

        // set mood reason
        TextView moodReason = dialog.findViewById(R.id.view_reason);
        moodReason.setText(mood.getReasonText());

        // close on click listener
        Button button = dialog.findViewById(R.id.close_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        // background to transparent so we only see our custom layout
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // set the background color based on the mood
        RelativeLayout relativeLayout = dialog.findViewById(R.id.viewLayout);

        if (mood.getEmotionText().equals("happy")){
            relativeLayout.setBackgroundResource(R.drawable.view_happy_bg);
        }
        else if (mood.getEmotionText().equals("angry")){
            relativeLayout.setBackgroundResource(R.drawable.view_angry_bg);
        }
        else if (mood.getEmotionText().equals("sad")){
            relativeLayout.setBackgroundResource(R.drawable.view_sad_bg);
        }
        else if (mood.getEmotionText().equals("afraid")){
            relativeLayout.setBackgroundResource(R.drawable.view_afraid_bg);
        }

        return dialog;

    }

}
