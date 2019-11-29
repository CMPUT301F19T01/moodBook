package com.example.moodbook.ui.friendMood;

import androidx.annotation.NonNull;

import com.example.moodbook.Mood;
import com.example.moodbook.MoodbookUser;

/**
 * This class handles everything about a friend's mood event.
 * @see MoodbookUser
 * @see Mood
 * @see java.lang.Comparable
 */
public class FriendMood implements Comparable<FriendMood> {
    private MoodbookUser user;
    private Mood mood;

    /**
     * This constructor is used to represent a mood event for a friend
     * @param user
     *  This is the friend user
     * @param mood
     *  This is the mood event
     */
    public FriendMood(@NonNull MoodbookUser user, @NonNull Mood mood) {
        this.user = user;
        this.mood = mood;
    }

    /**
     * This getter for username returns text
     * @return
     *  return username in text
     */
    public String getUsername() {
        return this.user.getUsername();
    }

    /**
     * This getter for uid returns text
     * @return
     *  returns uid in text
     */
    public String getUid() {
        return this.user.getUid();
    }

    /**
     * This getter for mood
     * @return
     *  returns user's mood object
     */
    public Mood getMood() {
        return this.mood;
    }

    /**
     * This compares friendMood based on mood
     * @param other
     *  friendMood object to be compared with
     * @return
     */
    @Override
    public int compareTo(@NonNull FriendMood other) {
        return this.mood.compareTo(other.getMood());
    }

    /**
     * This returns the string representation of friendMood
     * @return
     *  Returns username and mood in text to represent the friendMood
     */
    @NonNull
    @Override
    public String toString() {
        return this.getUsername()+","+this.getMood().toString();
    }
}
