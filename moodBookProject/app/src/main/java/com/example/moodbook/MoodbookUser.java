package com.example.moodbook;

import androidx.annotation.NonNull;

/**
 * Class for showing the available requests for a user
 */
public class MoodbookUser implements Comparable<MoodbookUser> {

    private String username;
    private String uid;

    public MoodbookUser(String username, String uid) {
        setUsername(username);
        setUid(uid);
    }

    public void setUsername(String username){
        this.username = username;
    }

    public String getUsername(){
        return this.username;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUid() {
        return this.uid;
    }

    @Override
    public int compareTo(@NonNull MoodbookUser other) {
        return this.username.compareTo(other.getUsername());
    }
}

