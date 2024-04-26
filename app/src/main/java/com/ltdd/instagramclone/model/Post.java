package com.ltdd.instagramclone.model;

public class Post {
    private String description;
    private String imageUrl;
    private String postId;
    private String postPublisherId;

    public Post(String description, String imageUrl, String postId, String postPublisherId) {
        this.description = description;
        this.imageUrl = imageUrl;
        this.postId = postId;
        this.postPublisherId = postPublisherId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getPostPublisherId() {
        return postPublisherId;
    }

    public void setPostPublisherId(String postPublisherId) {
        this.postPublisherId = postPublisherId;
    }
}
