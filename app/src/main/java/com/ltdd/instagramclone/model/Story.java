package com.ltdd.instagramclone.model;

public class Story {
    private String storyId;
    private String storyPublisherId;
    private String imageUrl;
    //Âm thanh nữa nếu có thể

    public Story(String storyId, String storyPublisherId, String imageUrl) {
        this.storyId = storyId;
        this.storyPublisherId = storyPublisherId;
        this.imageUrl = imageUrl;
    }

    public String getStoryId() {
        return storyId;
    }

    public void setStoryId(String storyId) {
        this.storyId = storyId;
    }

    public String getStoryPublisherId() {
        return storyPublisherId;
    }

    public void setStoryPublisherId(String storyPublisherId) {
        this.storyPublisherId = storyPublisherId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
