package com.example.moodbook;

import android.location.Location;

/**
 * subclass of Location to allow for storing the address on top of the location functionality
 */
public class MoodLocation extends Location {

    private String address;

    /**
     * construct new locaiton with a named provider
     * @param provider name of the location provider
     */
    public MoodLocation(String provider) {
        super(provider);
    }

    /**
     * sets address string
     * @param address the nearest address of the set of coordinates
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * gets address string
     * @return address string
     */
    public String getAddress() {
        return address;
    }
}
