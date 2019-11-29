package com.example.moodbook;

import androidx.annotation.NonNull;

/**
 * This is a moodBook user Object used for tracking requests
 * @see Mood
 * @see com.example.moodbook.ui.myFriends.MyFriendsFragment
 * @see com.example.moodbook.ui.followers.MyFollowersFragment
 */
public class MoodbookUser implements Comparable<MoodbookUser> {

    private String username;
    private String uid;

    /**
     * This create a new instance of the MoodbookUser Object
     * @param username
     *  The display name or username of the new user
     * @param uid
     *  The userID of the new user
     */
    public MoodbookUser(String username, String uid) {
        setUsername(username);
        setUid(uid);
    }

    /**
     * This sets the Username of a moodBookUser object
     * @param username
     *  The display name or username of the  user
     */
    public void setUsername(String username){
        this.username = username;
    }

    /**
     * This gets the Username of a moodBookUser
     * @return
     *  The display name or username of the  user
     */
    public String getUsername(){
        return this.username;
    }

    /**
     * This sets the user ID of a moodBookUser object
     * @param uid
     *  The user ID of the user
     */
    public void setUid(String uid) {
        this.uid = uid;
    }

    /**
     * This gets the user ID of a moodBookUser object
     * @return
     *  The user ID of the user
     */

    public String getUid() {
        return this.uid;
    }

    /**
     * This compares a user to another user
     * @param other
     *  The user we are comparing to
     * @return
     *  Return an int representing if one username was greater than ,equal to or less than the other alphabetically
     * @see Comparable
     */
    @Override
    public int compareTo(@NonNull MoodbookUser other) {
        return this.username.compareTo(other.getUsername());
    }
}

