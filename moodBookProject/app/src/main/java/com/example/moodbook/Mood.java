package com.example.moodbook;

import android.location.Location;
import android.media.Image;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class Mood {
    private Date date;
    private Date time;
    private String emotion_text;
    private String reason_text;     // optional
    private Image reason_photo;     // optional
    private String situation;       // optional
    private Location location;      // optional

    private final SimpleDateFormat dateFt;    // date format
    private final SimpleDateFormat timeFt;    // time format
    private final HashMap<String, Emotion> emotionMap;

    public Mood(String date_text, String time_text, String emotion,
                String reason_text, Image reason_photo,
                String situation, Location location) {
        // Initialize
        dateFt = new SimpleDateFormat ("yyyy-MM-dd");
        timeFt = new SimpleDateFormat ("HH:mm");
        emotionMap = new HashMap<>();
        emotionMap.put("happy",new Emotion("happy"));
        emotionMap.put("sad",new Emotion("sad"));
        emotionMap.put("angry",new Emotion("angry"));
        emotionMap.put("afraid",new Emotion("afraid"));

        setAll(date_text, time_text, emotion, reason_text, reason_photo, situation, location);
    }

    public Mood(String date_text, String time_text, String emotion) {
        this(date_text, date_text, emotion, null, null, null, null);
    }

    public void setAll(String date_text, String time_text, String emotion,
                       String reason_text, Image reason_photo,
                       String situation, Location location) {
        setDate(date_text);
        setTime(time_text);
        setEmotion(emotion);
        setReasonText(reason_text);
        setReasonPhoto(reason_photo);
        setSituation(situation);
        setLocation(location);
    }

    // Date
    public void setDate(String date_text) {
        // Initialize to current date
        if(date_text == null) {
            this.date = new Date();
        }
        else{
            try {
                this.date = dateFt.parse(date_text);
            }
            // Error: invalid argument
            catch (ParseException e) {
                // TODO
                e.printStackTrace();
            }
        }
    }

    public String getDateText() {
        return dateFt.format(this.date);
    }

    // Time
    public void setTime(String time_text) {
        // Initialize to current time
        if(time_text == null) {
            this.time = new Date();
        }
        else{
            try {
                this.time = timeFt.parse(time_text);
            }
            // Error: invalid argument
            catch (ParseException e) {
                // TODO
                e.printStackTrace();
            }
        }
    }

    public String getTimeText() {
        return timeFt.format(this.time);
    }

    // Emotion
    public void setEmotion(String emotion_text) {
        // Error: empty
        if(emotion_text == null) {
            // TODO
            return;
        }
        emotion_text = emotion_text.toLowerCase();
        // Valid argument
        if(emotionMap.containsKey(emotion_text)){
            this.emotion_text = emotion_text;
        }
        // Error: invalid argument
        else{
            // TODO
        }
    }

    public String getEmotionText() {
        return emotionMap.get(this.emotion_text).getName();
    }

    public Integer getEmotionImageResource() {
        Integer imageId = null;
        if(this.emotion_text != null){
            imageId = emotionMap.get(this.emotion_text).getImageId();
        }
        return imageId;
    }

    public Integer getEmotionColorResource() {
        Integer colorId = null;
        if(this.emotion_text != null){
            colorId = emotionMap.get(this.emotion_text).getColorId();
        }
        return colorId;
    }

    // Reason
    public void setReasonText(String reason_text) {
        // Check if text is longer than 20 characters or 3 words
        if(reason_text != null){
            // Error: > 20 characters
            if(reason_text.length() > 20){
                // TODO
                return;
            }
            else{
                String[] reason_text_words = reason_text.trim().split(" ");
                // Error: > 3 words
                if(reason_text_words.length > 3){
                    // TODO
                    return;
                }
            }
        }
        // Valid
        this.reason_text = reason_text;
    }

    public String getReasonText() {
        return this.reason_text;
    }

    public void setReasonPhoto(Image reason_photo) {
        this.reason_photo = reason_photo;
    }

    public Image getReasonPhoto() {
        return this.reason_photo;
    }

    // Situation
    public void setSituation(String situation) {
        this.situation = situation;
    }

    public String getSituation() {
        return this.situation;
    }

    // Location
    public void setLocation(Location location) {
        this.location = location;
    }

    public Location getLocation() {
        return this.location;
    }


    protected class Emotion{
        String name;
        Integer image_id;
        Integer color_id;

        public Emotion(String name) {
            this.name = name;
            setImageId();
            setColorId();
        }

        private void setImageId() {
            switch(this.name){
                case "happy":
                    this.image_id = R.drawable.happy;
                    break;
                case "sad":
                    this.image_id = R.drawable.sad;
                    break;
                case "angry":
                    this.image_id = R.drawable.angry;
                    break;
                case "afraid":
                    this.image_id = R.drawable.afraid;
                    break;
            }
        }

        private void setColorId() {
            switch(this.name){
                case "happy":
                    this.color_id = R.color.happyYellow;
                    break;
                case "sad":
                    this.color_id = R.color.sadBlue;
                    break;
                case "angry":
                    this.color_id = R.color.angryRed;
                    break;
                case "afraid":
                    this.color_id = R.color.afraidBrown;
                    break;
            }
        }

        public String getName() {
            return this.name;
        }

        public Integer getImageId() {
            return this.image_id;
        }

        public Integer getColorId() {
            return this.color_id;
        }
    }
}
