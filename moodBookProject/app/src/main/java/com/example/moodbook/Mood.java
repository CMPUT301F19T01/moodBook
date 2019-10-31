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
    private String doc_id;          // for editing & deleting

    // date time formatter
    public static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd");
    public static final SimpleDateFormat TIME_FORMATTER = new SimpleDateFormat("HH:mm");
    public static final SimpleDateFormat DATETIME_FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public Mood(String date_time_text, String emotion,
                String reason_text, Image reason_photo,
                String situation, Location location) throws MoodInvalidInputException {
        // Initialize
        setDateTime(date_time_text);
        setEmotion(emotion);
        setReasonText(reason_text);
        setReasonPhoto(reason_photo);
        setSituation(situation);
        setLocation(location);
    }

    public Mood(String date_time_text, String emotion) throws MoodInvalidInputException {
        this(date_time_text, emotion, null, null, null, null);
    }

    // Date
    public void setDateTime(String date_time_text) throws MoodInvalidInputException {
        // Error: empty
        if (date_time_text == null) {
            throw new MoodInvalidInputException("date_time","cannot be empty");
        }
        // Valid argument
        try {
            this.date_time = DATETIME_FORMATTER.parse(date_time_text);
        }
        // Error: invalid date time format
        catch (ParseException e) {
            throw new MoodInvalidInputException("date_time","must be yyyy-hh-dd hh:mm format");
        }
    }

    public String getDateText() {
        return DATE_FORMATTER.format(this.date_time);
    }

    public String getTimeText() {
        return TIME_FORMATTER.format(this.date_time);
    }

    public Date getDateTime() {
        return this.date_time;
    }

    // Emotion
    public void setEmotion(String emotion_text) throws MoodInvalidInputException {
        parseMoodEmotion(emotion_text);
        this.emotion_text = emotion_text.toLowerCase();
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
    public void setReasonText(String reason_text) throws MoodInvalidInputException {
        parseMoodReasonText(reason_text);
        this.reason_text = reason_text == "" ? null : reason_text;
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


    // Doc Id
    public void setDocId(String doc_id) {
        this.doc_id = doc_id;
    }

    public String getDoc_id() {
        return this.doc_id;
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

    @NonNull
    @Override
    public String toString() {
        return getDateText()+"_"+getTimeText()+"_"+getEmotionText();
    }


    public static Date parseMoodDate(String date_text) throws MoodInvalidInputException {
        Date date = null;
        // Error: empty
        if (date_text == null) {
            throw new MoodInvalidInputException("date","cannot be empty");
        }
        // Valid argument
        try {
            date = DATE_FORMATTER.parse(date_text);
        }
        // Error: invalid date format
        catch (ParseException e) {
            throw new MoodInvalidInputException("date","must be yyyy-hh-dd format");
        }
        return date;
    }

    public static Date parseMoodTime(String time_text) throws MoodInvalidInputException {
        Date time = null;
        // Error: empty
        if (time_text == null) {
            throw new MoodInvalidInputException("time","cannot be empty");
        }
        // Valid argument
        try {
            time = TIME_FORMATTER.parse(time_text);
        }
        // Error: invalid time format
        catch (ParseException e) {
            throw new MoodInvalidInputException("time","must be hh:mm format");
        }
        return time;
    }

    public static void parseMoodEmotion(String emotion_text) throws MoodInvalidInputException {
        // Error: empty
        if (emotion_text == null) {
            throw new MoodInvalidInputException("emotion","must be selected");
        }
        emotion_text = emotion_text.toLowerCase();
        // Error: no option is selected
        if (!Emotion.hasName(emotion_text)) {
            throw new MoodInvalidInputException("emotion","must be selected");
        }
    }

    public static void parseMoodReasonText(String reason_text) throws MoodInvalidInputException {
        // Check if text is longer than 20 characters or 3 words
        if (reason_text != null) {
            // Error: > 20 characters
            if (reason_text.length() > 20) {
                throw new MoodInvalidInputException("reason_text","cannot be longer than 20 characters");
            } else {
                String[] reason_text_words = reason_text.trim().split(" ");
                // Error: > 3 words
                if (reason_text_words.length > 3) {
                    throw new MoodInvalidInputException("reason_text","cannot have more than 3 words");
                }
            }
        }
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
