package com.example.moodbook.ui.friendMood;

import com.example.moodbook.Mood;

public class FriendMood {
    private String username;
    private String uid;
    private Mood mood;

    public FriendMood(String username, String uid) {
        this.username = username;
        this.uid = uid;
    }

    public Mood getMood() {
        return mood;
    }

    public void setMood(Mood mood) {
        this.mood = mood;
    }

    public String getUsername() {
        return username;
    }

    public String getUid() {
        return uid;
    }
}
