package com.example.loginpage.ModelClass;

public class FriendHomemodel {

    String username,profileImageUrl,id;

    public FriendHomemodel() {
    }

    public FriendHomemodel(String username, String profileImageUrl, String id) {
        this.username = username;
        this.profileImageUrl = profileImageUrl;
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
