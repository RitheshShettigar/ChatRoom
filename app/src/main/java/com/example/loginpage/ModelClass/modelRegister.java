package com.example.loginpage.ModelClass;



public class modelRegister {

    String username;
    String Email;
    String imageuri;
    String id;

    public modelRegister(String username, String finalImageUri) {
    }

    public modelRegister(String username, String Email, String imageuri, String id) {
        this.username = username;
        this.Email = Email;
        this.imageuri = imageuri;
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String Email) {
        this.Email = Email;
    }

    public String getImageuri() {
        return imageuri;
    }

    public void setImageuri(String imageuri) {
        this.imageuri = imageuri;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
