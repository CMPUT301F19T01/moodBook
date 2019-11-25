package com.example.moodbook;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * super class that is extended by MyMoodMapFragment and FriendMoodMapFragment
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

        TextView view_friend_name, view_date_time, view_emotion,
                view_reason, view_situation, view_location;
        ImageView view_emoji, view_uploaded_pic;
        Button view_edit_button, view_cancel_button;
        ScrollView view_scrollView;

        view_friend_name = dialog.findViewById(R.id.view_friend_name);
        view_date_time = dialog.findViewById(R.id.view_date_time);
        view_reason = dialog.findViewById(R.id.view_reason);
        view_situation = dialog.findViewById(R.id.view_situation);
        view_location = dialog.findViewById(R.id.view_location);
        view_emotion = dialog.findViewById(R.id.view_emotion);
        view_emoji = dialog.findViewById(R.id.view_emoji);
        view_edit_button = dialog.findViewById(R.id.view_edit_button);
        view_cancel_button = dialog.findViewById(R.id.view_cancel_button);
        view_uploaded_pic = dialog.findViewById(R.id.view_uploaded_pic);
        view_scrollView = dialog.findViewById(R.id.viewPage);

        // Hide edit button
        Button editButton = dialog.findViewById(R.id.view_edit_button);
        editButton.setVisibility(View.GONE);

        // check if we are drawing a friend mood or own users mood
        if(username != null){
            view_friend_name.setVisibility(View.VISIBLE);
            view_friend_name.setText(username);
        }

        // show mood date time
        view_date_time.setText("Created: " + mood.getDateText() +" at " + mood.getTimeText() );

        // show mood reason
        view_reason.setText("Reason: "+((mood.getReasonText()==null)?"N/A":mood.getReasonText()));

        // show mood location
        view_location.setText("Location:  "+ ((mood.getLocation()==null)?"N/A":mood.getLocation().getAddress()));

        // show mood situation
        view_situation.setText("Situation:  " + ((mood.getSituation()==null)?"N/A":mood.getSituation()));

        // show mood emotion
        view_emotion.setText(mood.getEmotionText());
        //Set emoji
        if(Mood.Emotion.hasName(mood.getEmotionText())) {
            view_emoji.setImageResource(Mood.Emotion.getImageResourceId(mood.getEmotionText()));
            view_scrollView.setBackgroundColor(getResources().getColor(
                    Mood.Emotion.getColorResourceId(mood.getEmotionText())
            ));
        }
        dbMoodSetter.getImageFromDB(mood.getDocId(), view_uploaded_pic);


        // close on click listener
        view_cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        // background to transparent so we only see our custom layout
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        return dialog;

    }

}
