package com.example.moodbook.ui.friendMood;

import androidx.annotation.NonNull;

import com.example.moodbook.Mood;

public class FriendMood implements Comparable<FriendMood> {
    private String username;
    private String uid;
    private Mood mood;

    public FriendMood(String username, String uid, Mood mood) {
        this.username = username;
        this.uid = uid;
        this.mood = mood;
    }

    public String getUsername() {
        return this.username;
    }

    public String getUid() {
        return this.uid;
    }

    public Mood getMood() {
        return this.mood;
    }

    @Override
    public int compareTo(@NonNull FriendMood other) {
        return this.mood.compareTo(other.getMood());
    }

    @NonNull
    @Override
    public String toString() {
        return this.getUsername()+","+this.getMood().toString();
    }
}
