package com.example.moodbook;

import android.location.Location;

/**
 * subclass of Location to allow for storing the address along with the other location functionality
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

    /**
     * gets latitude text
     * @return returns latitude string
     */
    public String getLatitudeText() {
        return ((Double)super.getLatitude()).toString();
    }

    /**
     * gets longitude text
     * @return returns longitude string
     */
    public String getLongitudeText() {
        return ((Double)super.getLongitude()).toString();
    }
}
