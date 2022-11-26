package com.example.loginpage.ModelClass;

public class Commentmodel {

    String username,comment,profile;

    public Commentmodel(String username, String comment, String profile) {
        this.username = username;
        this.comment = comment;
        this.profile = profile;
    }

    public Commentmodel() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }
}
