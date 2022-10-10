package com.example.loginpage.ModelClass;

import java.util.ArrayList;

public class UserStatus {
    private  String username,profileImage;
    private long lastUpdate;
    private ArrayList<Status>statuses;

    public UserStatus() {
    }

    public UserStatus(String username, String profileImage, long lastUpdate, ArrayList<Status> statuses) {
        this.username = username;
        this.profileImage = profileImage;
        this.lastUpdate = lastUpdate;
        this.statuses = statuses;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public long getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(long lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public ArrayList<Status> getStatuses() {
        return statuses;
    }

    public void setStatuses(ArrayList<Status> statuses) {
        this.statuses = statuses;
    }
}
