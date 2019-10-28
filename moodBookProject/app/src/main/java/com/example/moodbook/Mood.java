package com.example.moodbook;

import android.media.Image;

import com.google.android.gms.maps.model.LatLng;

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
    private LatLng latLng;      // optional

    private  SimpleDateFormat dateFt;    // date format
    private  SimpleDateFormat timeFt;    // time format
    private HashMap<String, Emotion> emotionMap;

    public Mood(Date date, Date time, String emotion,
                String reason_text, Image reason_photo,
                String situation, LatLng latLng) {
        // Initialize
        dateFt = new SimpleDateFormat ("yyyy-MM-dd");
        timeFt = new SimpleDateFormat ("HH:mm");
        emotionMap = new HashMap<>();
        emotionMap.put("happy",new Emotion("happy"));
        emotionMap.put("sad",new Emotion("sad"));
        emotionMap.put("angry",new Emotion("angry"));
        emotionMap.put("afraid",new Emotion("afraid"));

        setAll(date, time, emotion, reason_text, reason_photo, situation, latLng);
    }

    public Mood(Date date, Date time, String emotion) {
        this(date, time, emotion, null, null, null, null);
    }

    // constructor to test moodMaps
    public Mood(String emotion, LatLng latLng){
        this.latLng = latLng;
        this.emotion_text = emotion;
    }

    public void setAll(Date date, Date time, String emotion,
                       String reason_text, Image reason_photo,
                       String situation, LatLng latLng) {
        setDate(date);
        setTime(time);
        setEmotion(emotion);
        setReasonText(reason_text);
        setReasonPhoto(reason_photo);
        setSituation(situation);
        setlatLng(latLng);
    }

    // Date
    public void setDate(Date date) {
        // Initialize to current date
        if(date == null) {
            date = new Date();
        }
        this.date = date;
    }

    public String getDateText() {
        return dateFt.format(this.date);
    }

    // Time
    public void setTime(Date time) {
        // Initialize to current time
        if(time == null) {
            time = new Date();
        }
        this.time = time;
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

    // latLng
    public void setlatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public LatLng getlatLng() {
        return this.latLng;
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
