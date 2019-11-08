package com.example.moodbook.ui.myRequests;

/**
 * Class for showing the available requests for a user
 */
public class RequestUser {

    private String username;

    public RequestUser(String username) {

        setUsername(username);
    }

    public void setUsername(String username){
        this.username = username;
    }

    public String getUsername(){
        return this.username;
    }

}
