package com.example.moodbook;

import android.app.Dialog;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * super class that is extended by MyMoodMapFragment and FriendMoodMapFragment
 * that contains shared functionality between the two classes
 */
public class MoodMapFragment extends PageFragment{

    /**
     * Binds the mood data to a dialog for viewing on map view
     *
     * @param mood
     *  the mood who's data we are binding
     * @param dialog
     *  the dialog that contains the views where we want to bind the mood data
     * @param dbMoodSetter
     *  uses a "getImageFromDB" method from this object to get image
     * @return
     *  the dialog with all the mood data binded to their respective views
     */
    protected Dialog bindViews(Mood mood, final Dialog dialog, DBMoodSetter dbMoodSetter, String username){

        // change dialog content to be custom view layout
        dialog.setContentView(R.layout.activity_view_mood);

        // declare view variables
        TextView view_friend_name;
        TextView view_date_time;
        TextView view_emotion;
        TextView view_reason;
        TextView view_situation;
        TextView view_location;
        ImageView view_emoji;
        ImageView view_uploaded_pic;
        Button view_cancel_button;
        ScrollView view_scrollView;

        // find all the views
        view_friend_name = dialog.findViewById(R.id.view_friend_name);
        view_date_time = dialog.findViewById(R.id.view_date_time);
        view_reason = dialog.findViewById(R.id.view_reason);
        view_situation = dialog.findViewById(R.id.view_situation);
        view_location = dialog.findViewById(R.id.view_location);
        view_emotion = dialog.findViewById(R.id.view_emotion);
        view_emoji = dialog.findViewById(R.id.view_emoji);
        view_cancel_button = dialog.findViewById(R.id.view_cancel_button);
        view_uploaded_pic = dialog.findViewById(R.id.view_uploaded_pic);
        view_scrollView = dialog.findViewById(R.id.viewPage);
        Button editButton = dialog.findViewById(R.id.view_edit_button);
        editButton.setVisibility(View.GONE);

        // check if we need to show username for friend mood
        if(username != null){
            view_friend_name.setVisibility(View.VISIBLE);
            view_friend_name.setText(username);
        }

        // show mood date time
        view_date_time.setText(mood.getDateText() +" at " + mood.getTimeText() );

        // show mood reason
        view_reason.setText((mood.getReasonText()==null)?"N/A":mood.getReasonText());

        // show mood location
        view_location.setText((mood.getLocation()==null)?"N/A":mood.getLocation().getAddress());

        // show mood situation
        view_situation.setText((mood.getSituation()==null)?"N/A":mood.getSituation());

        // show mood emotion
        view_emotion.setText(mood.getEmotionText());
        if(Mood.Emotion.hasName(mood.getEmotionText())) {
            view_emoji.setImageResource(Mood.Emotion
                    .getImageResourceId(mood.getEmotionText()));
            view_scrollView.setBackgroundColor(getResources().getColor(
                    Mood.Emotion.getColorResourceId(mood.getEmotionText())
            ));
        }

        // get image from database
        dbMoodSetter.getImageFromDB(mood.getDocId(), view_uploaded_pic);

        // set functionality for button to close dialog
        view_cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        return dialog;

    }

}
