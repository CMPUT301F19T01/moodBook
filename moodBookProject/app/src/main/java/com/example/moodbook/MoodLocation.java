package com.example.moodbook;

import android.location.Location;

public class MoodLocation extends Location {

    private String address;

    public MoodLocation(String provider) {
        super(provider);
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }
}
