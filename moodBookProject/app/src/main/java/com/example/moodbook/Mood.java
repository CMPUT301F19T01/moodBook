package com.example.moodbook;

import android.location.Location;
import android.media.Image;

import androidx.annotation.NonNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class Mood implements Comparable<Mood> {
    private Date date_time;         // mandatory
    private String emotion_text;    // mandatory
    private String reason_text;     // optional
    private Image reason_photo;     // optional
    private String situation;       // optional
    private Location location;      // optional

    private final SimpleDateFormat dateFt;      // date format
    private final SimpleDateFormat timeFt;      // time format
    private final SimpleDateFormat dateTimeFt;  // date_time format

    public Mood(String date_time_text, String emotion,
                String reason_text, Image reason_photo,
                String situation, Location location) {
        // Initialize
        dateFt = new SimpleDateFormat("yyyy-MM-dd");
        timeFt = new SimpleDateFormat("HH:mm");
        dateTimeFt = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        setAll(date_time_text, emotion, reason_text, reason_photo, situation, location);
    }

    public Mood(String date_time_text, String emotion) {
        this(date_time_text, emotion, null, null, null, null);
    }

    public void setAll(String date_time_text, String emotion,
                       String reason_text, Image reason_photo,
                       String situation, Location location) {
        setDateTime(date_time_text);
        setEmotion(emotion);
        setReasonText(reason_text);
        setReasonPhoto(reason_photo);
        setSituation(situation);
        setLocation(location);
    }

    // Date
    public void setDateTime(String date_time_text) {
        // Initialize to current date
        if (date_time_text == null) {
            this.date_time = new Date();
        } else {
            try {
                this.date_time = dateTimeFt.parse(date_time_text);
            }
            // Error: invalid argument
            catch (ParseException e) {
                // TODO
                e.printStackTrace();
            }
        }
    }

    public String getDateText() {
        return dateFt.format(this.date_time);
    }

    public String getTimeText() {
        return timeFt.format(this.date_time);
    }

    public Date getDateTime() {
        return this.date_time;
    }

    // Emotion
    public void setEmotion(String emotion_text) {
        // Error: empty
        if (emotion_text == null) {
            // TODO
            return;
        }
        emotion_text = emotion_text.toLowerCase();
        // Valid argument
        if (Emotion.hasName(emotion_text)) {
            this.emotion_text = emotion_text;
        }
        // Error: invalid argument
        else {
            // TODO
        }
    }

    public String getEmotionText() {
        return this.emotion_text;
    }

    public Integer getEmotionImageResource() {
        return Emotion.getImageResourceId(this.emotion_text);
    }

    public Integer getEmotionColorResource() {
        return Emotion.getColorResourceId(this.emotion_text);
    }


    // Reason
    public void setReasonText(String reason_text) {
        // Check if text is longer than 20 characters or 3 words
        if (reason_text != null) {
            // Error: > 20 characters
            if (reason_text.length() > 20) {
                // TODO
                return;
            } else {
                String[] reason_text_words = reason_text.trim().split(" ");
                // Error: > 3 words
                if (reason_text_words.length > 3) {
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


    @Override
    public int compareTo(@NonNull Mood other) {
        if (this == other) return 0;
        Date dateTime = this.getDateTime();
        Date otherDateTime = other.getDateTime();
        // smaller if dateTime for this object has parsing error
        if (dateTime == null) return -1000;
        // larger if dateTime for other object has parsing error
        if (otherDateTime == null) return 1000;

        return dateTime.compareTo(otherDateTime);
    }


    public static class Emotion {

        private static String[] names;
        private static int[] image_resource_id;
        private static int[] color_resource_id;
        private static HashMap<String, Integer> name_index;

        static {
            names = new String[]{"happy", "sad", "angry", "afraid"};
            image_resource_id = new int[]{
                    R.drawable.happy, R.drawable.sad, R.drawable.angry, R.drawable.afraid};
            color_resource_id = new int[]{
                    R.color.happyYellow, R.color.sadBlue, R.color.angryRed, R.color.afraidBrown};
            // map emotion name to index
            name_index = new HashMap<>();
            for (int i = 0; i < names.length; i++) {
                name_index.put(names[i], i);
            }
        }

        public static Boolean hasName(String name) {
            return name_index.containsKey(name);
        }

        public static Integer getImageResourceId(String name) {
            Integer resourceId = null;
            if (name_index.containsKey(name)) {
                resourceId = image_resource_id[name_index.get(name)];
            }
            return resourceId;
        }

        public static Integer getColorResourceId(String name) {
            Integer resourceId = null;
            if (name_index.containsKey(name)) {
                resourceId = color_resource_id[name_index.get(name)];
            }
            return resourceId;
        }

        public static String[] getNames() {
            return names.clone();
        }

        public static int[] getImageResources() {
            return image_resource_id.clone();
        }

        public static int[] getColorResources() {
            return color_resource_id.clone();
        }

    }
}
