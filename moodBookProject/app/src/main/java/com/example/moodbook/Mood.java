package com.example.moodbook;

import android.graphics.Bitmap;
import android.location.Location;
import androidx.annotation.NonNull;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * This class handles everything about a mood event and ensures inputs for mood fields are valid.
 * @see MoodListAdapter
 * @see CreateMoodActivity
 * @see EditMoodActivity
 * @see java.lang.Comparable
 */
public class Mood implements Comparable<Mood> {
    private Date date_time;         // mandatory
    private String emotion_text;    // mandatory
    private String reason_text;     // optional
    private Bitmap reason_photo;    // optional
    private String situation;       // optional
    private Location location;      // optional
    private String doc_id;          // document id in db for editing/deleting the mood

    // date time formatter
    public static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd");
    public static final SimpleDateFormat TIME_FORMATTER = new SimpleDateFormat("HH:mm");
    public static final SimpleDateFormat DATETIME_FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    /**
     * This constructor is used to represent a mood event
     * @param date_time_text
     *  This is the date and time when the mood event occurs
     * @param emotion
     *  This is the emotional state when the mood event occurs
     * @param reason_text
     *  This is the reason why the mood event occurs in text
     * @param reason_photo
     *  This is the reason why the mood event occurs in photo
     * @param situation
     *  This is the social situation when the mood event occurs
     * @param location
     *  This is the location where the mood event occurs
     * @throws MoodInvalidInputException
     */
    public Mood(String date_time_text, String emotion,
                String reason_text, Bitmap reason_photo,
                String situation, Location location) throws MoodInvalidInputException {
        // Initialize all the fields for the mood event
        setDateTime(date_time_text);
        setEmotion(emotion);
        setReasonText(reason_text);
        setReasonPhoto(reason_photo);
        setSituation(situation);
        setLocation(location);
    }

    /**
     * This setter for date_time ensures text in date_time format and parses it into Date
     * @param date_time_text
     *  This is the date and time of the mood event in text
     * @throws MoodInvalidInputException
     */
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

    /**
     * This getter for date returns text in date format
     * @return
     *  Returns date as formatted text
     */
    public String getDateText() {
        return DATE_FORMATTER.format(this.date_time);
    }

    /**
     * This getter for time returns text in time format
     * @return
     *  Returns time as formatted text
     */
    public String getTimeText() {
        return TIME_FORMATTER.format(this.date_time);
    }

    /**
     * This getter for date_time returns Date
     * @return
     *  Return date & time as Date
     */
    public Date getDateTime() {
        return this.date_time;
    }


    /**
     * This setter for emotion ensures text means valid emotional state
     * @param emotion_text
     *  This is the emotion state of the mood event in text
     * @throws MoodInvalidInputException
     */
    public void setEmotion(String emotion_text) throws MoodInvalidInputException {
        parseMoodEmotion(emotion_text);
        this.emotion_text = emotion_text.toLowerCase();
    }

    /**
     * This getter for emotion returns text
     * @return
     *  Returns emotion as text
     */
    public String getEmotionText() {
        return this.emotion_text;
    }

    /**
     * This getter for emotion returns image resource id
     * @return
     *  Returns image resource id of emotion
     */
    public Integer getEmotionImageResource() {
        return Emotion.getImageResourceId(this.emotion_text);
    }

    /**
     * This getter for emotion returns color resource id
     * @return
     *  Returns color resource id of emotion
     */
    public Integer getEmotionColorResource() {
        return Emotion.getColorResourceId(this.emotion_text);
    }


    /**
     * This setter for reason_text ensures the length of text is <= 20 characters and <= 3 words
     * @param reason_text
     *  This is the reason of the mood event in text
     * @throws MoodInvalidInputException
     */
    public void setReasonText(String reason_text) throws MoodInvalidInputException {
        parseMoodReasonText(reason_text);
        this.reason_text = reason_text == "" ? null : reason_text;
    }

    /**
     * This getter for reason_text return text
     * @return
     *  Returns reason in text
     */
    public String getReasonText() {
        return this.reason_text;
    }

    /**
     * This setter for reason_photo accepts Bitmap
     * @param reason_photo
     *  This is the reason of the mood event in photo
     */
    public void setReasonPhoto(Bitmap reason_photo) {
        this.reason_photo = reason_photo;
    }

    /**
     * This getter for reason_photo returns Bitmap
     * @return
     *  Returns reason in photo as Bitmap
     */
    public Bitmap getReasonPhoto() {
        return this.reason_photo;
    }


    /**
     * This setter for situation accepts text
     * @param situation
     *  This is the social situation of the mood event in text
     */
    public void setSituation(String situation) {
        this.situation = situation;
    }

    /**
     * This getter for situation returns text
     * @return
     *  Returns social situation in text
     */
    public String getSituation() {
        return this.situation;
    }


    /**
     * This setter for location accepts Location
     * @param location
     *  This is the location of the mood event in Location
     */
    public void setLocation(Location location) {
        this.location = location;
    }

    /**
     * This getter for location returns Location
     * @return
     *  Returns location as Location
     */
    public Location getLocation() {
        return this.location;
    }


    /**
     * This setter for doc_id accepts text (generated from db counter)
     * @param doc_id
     *  This is the document id of the mood in db
     */
    public void setDocId(String doc_id) {
        this.doc_id = doc_id;
    }

    /**
     * This getter for doc_id returns text
     * @return
     *  Returns document id in text
     */
    public String getDocId() {
        return this.doc_id;
    }


    /**
     * This compares this mood with the other mood based on date_time;
     * the mood with earlier date_time is smaller
     * @param other
     *  This is the other mood to be compared with
     * @return
     *  1 if date_time of this mood is later than date_time of other mood
     *  0 if date_time of this mood is same as date_time of other mood
     *  -1 if date_time of this mood is earlier than date_time of other mood
     */
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

    /**
     * This returns the string representation of the mood
     * @return
     *  Returns date, time and emotion in text to represent the mood
     */
    @NonNull
    @Override
    public String toString() {
        return getDateText()+"_"+getTimeText()+"_"+getEmotionText();
    }


    public static String locationToText(Location location) {
        String location_text = null;
        if(location != null) {
            location_text = location.getLatitude() + " , " + location.getLongitude();
        }
        return location_text;
    }


    /**
     * This helper method parses data_text into Date, and is used by CreateMoodActivity
     * @param date_text
     *  This is date in text
     * @return
     *  Returns parsed Date if text is valid; otherwise, throw exception
     * @throws MoodInvalidInputException
     */
    public static Date parseMoodDate(String date_text) throws MoodInvalidInputException {
        Date date;
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

    /**
     * This helper method parses time_text into Date, and is used by CreateMoodActivity
     * @param time_text
     *  This is time in text
     * @return
     *  Returns parsed Date if text is valid; otherwise, throw exception
     * @throws MoodInvalidInputException
     */
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

    /**
     * This helper method ensures emotion is valid, and is used by CreateMoodActivity
     * @param emotion_text
     *  This is emotion in text
     * @throws MoodInvalidInputException
     */
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

    /**
     * This helper method ensures reason in text is valid, and is used by CreateMoodActivity
     * @param reason_text
     *  This is reason in text
     * @throws MoodInvalidInputException
     */
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


    /**
     * This static class handles everything about emotional state, and is used by Mood and MoodEditor.
     * @see Mood
     * @see MoodEditor
     */
    public static class Emotion {

        private static String[] names;
        private static int[] image_resource_id;
        private static int[] color_resource_id;
        private static HashMap<String, Integer> name_index;

        /**
         * This static constructor initializes names, image_resource_id, color_resource_id for
         * all 4 emotional states
         */
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

        /**
         * This determine if given text is a name of a valid emotional state
         * @param name
         *  This is the given name of an emotional state
         * @return
         *  Returns true if text is valid, false otherwise
         */
        public static Boolean hasName(String name) {
            return name_index.containsKey(name);
        }

        /**
         * This getter returns the image_resource_id for the given name of a emotional state
         * @param name
         *  This is the given name of an emotional state
         * @return
         *  Returns image_resource_id if the given text is valid, null otherwise
         */
        public static Integer getImageResourceId(String name) {
            Integer resourceId = null;
            if (name_index.containsKey(name)) {
                resourceId = image_resource_id[name_index.get(name)];
            }
            return resourceId;
        }

        /**
         * This getter returns the color_resource_id for the given name of a emotional state
         * @param name
         *  This is the given name of an emotional state
         * @return
         *  Returns color_resource_id if the given text is valid, null otherwise
         */
        public static Integer getColorResourceId(String name) {
            Integer resourceId = null;
            if (name_index.containsKey(name)) {
                resourceId = color_resource_id[name_index.get(name)];
            }
            return resourceId;
        }

        /**
         * This getter returns the copy of names of all emotional states
         * @return
         *  Returns the copy of list of names
         */
        public static String[] getNames() {
            return names.clone();
        }

        /**
         * This getter returns the copy of image_resource_id of all emotional states
         * @return
         *  Returns the copy of list of image_resource_id
         */
        public static int[] getImageResources() {
            return image_resource_id.clone();
        }

        /**
         * This getter returns the copy of color_resource_id of all emotional states
         * @return
         *  Returns the copy of list of color_resource_id
         */
        public static int[] getColorResources() {
            return color_resource_id.clone();
        }

    }
}
