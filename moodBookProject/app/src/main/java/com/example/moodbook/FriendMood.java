package com.example.moodbook;

public class FriendMood {
    private String user_name;
    private String uid;
    private Mood mood;

    public FriendMood(String user_name, String uid) {
        this.user_name = user_name;
        this.uid = uid;
    }

    public Mood getMood() {
        return mood;
    }

    public void setMood(Mood mood) {
        this.mood = mood;
    }
}
