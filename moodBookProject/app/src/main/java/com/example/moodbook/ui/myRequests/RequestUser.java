package com.example.moodbook.ui.myRequests;

/**
 * Class for showing the available requests for a user
 */
public class RequestUser {

    private String username;
    private String uid;

    public RequestUser(String username, String uid) {

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
}

