package com.example.moodbook.ui.friendMood;

import androidx.annotation.NonNull;

import com.example.moodbook.Mood;
import com.example.moodbook.User;

public class FriendMood implements Comparable<FriendMood> {
    private User user;
    private Mood mood;

    public FriendMood(String username, String uid, Mood mood) {
        this.user = new User(username, uid);
        this.mood = mood;
    }

    public String getUsername() {
        return this.user.getUsername();
    }

    public String getUid() {
        return this.user.getUid();
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
