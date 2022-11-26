package com.example.loginpage.ModelClass;

public class  PostModel {

    String date,postImage,postDesc,userProfile,userName,postid,userid;

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public PostModel() {
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPostImage() {
        return postImage;
    }

    public void setPostImage(String postImage) {
        this.postImage = postImage;
    }

    public String getPostDesc() {
        return postDesc;
    }

    public void setPostDesc(String postDesc) {
        this.postDesc = postDesc;
    }

    public String getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(String userProfile) {
        this.userProfile = userProfile;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPostid() {
        return postid;
    }



    public PostModel(String date, String postImage, String postDesc, String userProfile, String userName) {
        this.date = date;
        this.postImage = postImage;
        this.postDesc = postDesc;
        this.userProfile = userProfile;
        this.userName = userName;


    }
}
