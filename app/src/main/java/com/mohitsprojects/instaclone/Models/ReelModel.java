package com.mohitsprojects.instaclone.Models;

public class ReelModel {

    String username;
    String caption;
    String userImageUrl;
    boolean isLiked;
    String VideoUrl;

    public ReelModel(String username, String caption, String userImageUrl, boolean isLiked, String videoUrl) {
        this.username = username;
        this.caption = caption;
        this.userImageUrl = userImageUrl;
        this.isLiked = isLiked;
        VideoUrl = videoUrl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getUserImageUrl() {
        return userImageUrl;
    }

    public void setUserImageUrl(String userImageUrl) {
        this.userImageUrl = userImageUrl;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }

    public String getVideoUrl() {
        return VideoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        VideoUrl = videoUrl;
    }
}
