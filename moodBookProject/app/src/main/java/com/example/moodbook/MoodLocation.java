package com.example.moodbook;

import android.location.Location;

/**
 * This is a subclass of Location to allow for storing the address along with the other location functionality
 * @see Location
 */
public class MoodLocation extends Location {

    private String address;

    /**
     * This constructs new locaiton with a named provider
     * @param provider
     * This is the name of the location provider
     */
    public MoodLocation(String provider) {
        super(provider);
    }

    /**
     * This sets address string
     * @param address
     *  This is the nearest address of the set of coordinates
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * This gets address string
     * @return
     *  Returns the address string
     */
    public String getAddress() {
        return address;
    }

    /**
     * This gets latitude text
     * @return
     *  Returns latitude string
     */
    public String getLatitudeText() {
        return ((Double)super.getLatitude()).toString();
    }

    /**
     * This gets longitude text
     * @return
     *  Returns longitude string
     */
    public String getLongitudeText() {
        return ((Double)super.getLongitude()).toString();
    }
}
