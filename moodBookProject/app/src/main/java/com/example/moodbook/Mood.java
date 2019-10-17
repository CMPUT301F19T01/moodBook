package com.example.moodbook;

import android.location.Location;
import android.media.Image;

import java.util.Date;

public class Mood {
    private Date date;
    private Date time;
    private String emotional_state;
    private String reason_text;
    private Image reason_photo;     // string of image name?
    private String situation;
    private Location location;


    public Mood(Date date, Date time, String emotional_state,
                String reason_text, Image reason_photo,
                String situation, Location location) {
        setAll(date, time, emotional_state, reason_text, reason_photo, situation, location);
    }

    public void setAll(Date date, Date time, String emotional_state,
                  String reason_text, Image reason_photo,
                  String situation, Location location) {
        setDate(date);
        setTime(time);
        setEmotional_state(emotional_state);
        setReason_text(reason_text);
        setReason_photo(reason_photo);
        setSituation(situation);
        setLocation(location);
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public void setEmotional_state(String emotional_state) {
        this.emotional_state = emotional_state;
    }

    public void setReason_text(String reason_text) {
        this.reason_text = reason_text;
    }

    public void setReason_photo(Image reason_photo) {
        this.reason_photo = reason_photo;
    }

    public void setSituation(String situation) {
        this.situation = situation;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
