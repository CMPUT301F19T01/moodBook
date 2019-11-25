package com.example.moodbook;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

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
    protected Dialog bindViews(Mood mood, final Dialog dialog, DBMoodSetter dbMoodSetter, String username){
        // set dialog to custom layout
        dialog.setContentView(R.layout.activity_view_mood);

        // check if we are drawing a friend mood or own users mood
        if(username != null){
            TextView name = dialog.findViewById(R.id.view_friend_name);
            name.setText(username);
            name.setVisibility(View.VISIBLE);
        }

        // hide edit button
        Button editButton = dialog.findViewById(R.id.edit);
        editButton.setVisibility(View.GONE);

        // show the reason photo
        ImageView reasonPhoto = dialog.findViewById(R.id.view_uploaded_pic);
        dbMoodSetter.getImageFromDB(mood.getDocId(), reasonPhoto);

        // set mood emotion image
        ImageView imageView = dialog.findViewById(R.id.view_emoji);
        imageView.setImageResource(mood.getEmotionImageResource());

        // set mood text
        TextView moodText = dialog.findViewById(R.id.view_emotion);
        moodText.setText(mood.getEmotionText());

        // set mood date time
        TextView moodDate = dialog.findViewById(R.id.view_date_time);
        moodDate.setText(mood.getDateText() + " " + mood.getTimeText());

        // set mood location
        TextView moodLocation = dialog.findViewById(R.id.view_location);
        String address = mood.getLocation().getAddress();
        moodLocation.setText(address);

        // set mood situation
        TextView moodSituation = dialog.findViewById(R.id.view_situation);
        String situation = mood.getSituation();
        if (situation == null){
            situation = "N/A";
        }
        moodSituation.setText("Situation: " + situation);

        // set mood reason
        TextView moodReason = dialog.findViewById(R.id.view_reason);
        String reason = mood.getReasonText();
        if(reason == null){
            reason = "N/A";
        }
        moodReason.setText("Reason: " + reason);

        // close on click listener
        Button button = dialog.findViewById(R.id.cancel_view);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        // background to transparent so we only see our custom layout
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ScrollView scrollView = dialog.findViewById(R.id.viewPage);
        scrollView.setBackgroundColor(Color.TRANSPARENT);

        // set the background color based on the mood
        LinearLayout linearLayout= dialog.findViewById(R.id.view_layout);

        if (mood.getEmotionText().equals("happy")){
            linearLayout.setBackgroundResource(R.drawable.view_happy_bg);
        }
        else if (mood.getEmotionText().equals("angry")){
            linearLayout.setBackgroundResource(R.drawable.view_angry_bg);
        }
        else if (mood.getEmotionText().equals("sad")){
            linearLayout.setBackgroundResource(R.drawable.view_sad_bg);
        }
        else if (mood.getEmotionText().equals("afraid")){
            linearLayout.setBackgroundResource(R.drawable.view_afraid_bg);
        }

        return dialog;

    }

}
