package com.ltdd.instagramclone.model;

public class Like {
    String UserID_like;

    public Like(String userID_like) {
        UserID_like = userID_like;
    }

    public Like() {
    }

    public String getUserID_like() {
        return UserID_like;
    }

    public void setUserID_like(String userID_like) {
        UserID_like = userID_like;
    }
}
