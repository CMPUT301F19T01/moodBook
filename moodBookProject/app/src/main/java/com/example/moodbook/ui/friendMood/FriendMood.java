package com.example.moodbook.ui.friendMood;

import androidx.annotation.NonNull;

import com.example.moodbook.Mood;
import com.example.moodbook.MoodbookUser;

/**
 *
 */
public class FriendMood implements Comparable<FriendMood> {
    private MoodbookUser user;
    private Mood mood;

    /**
     *
     * @param user
     * @param mood
     */
    public FriendMood(@NonNull MoodbookUser user, @NonNull Mood mood) {
        this.user = user;
        this.mood = mood;
    }

    /**
     * get the users name
     * @return
     *  return user name string
     */
    public String getUsername() {
        return this.user.getUsername();
    }

    /**
     * get the users id
     * @return
     *  returns users Id
     */
    public String getUid() {
        return this.user.getUid();
    }

    /**
     * get the users mood
     * @return
     *  returns user's mood object
     */
    public Mood getMood() {
        return this.mood;
    }

    /**
     *
     * @param other
     *  friend mood object
     * @return
     */
    @Override
    public int compareTo(@NonNull FriendMood other) {
        return this.mood.compareTo(other.getMood());
    }

    /**
     *
     * @return
     *  return string of username and mood
     */
    @NonNull
    @Override
    public String toString() {
        return this.getUsername()+","+this.getMood().toString();
    }
}
