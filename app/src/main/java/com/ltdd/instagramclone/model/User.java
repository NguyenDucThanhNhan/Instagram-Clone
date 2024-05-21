package com.ltdd.instagramclone.model;

public class User {
    private String name;
    private String email;
    private String username;
    private String bio;
    private String imageurl;
    private String userid;

    public User(String name, String email, String username, String bio, String imageurl, String userid) {
        this.name = name;
        this.email = email;
        this.username = username;
        this.bio = bio;
        this.imageurl = imageurl;
        this.userid = userid;
    }
    public User(){

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }
}
