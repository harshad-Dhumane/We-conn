package com.mohitsprojects.instaclone.Models;

public class PostModel {
    String imageurl;
    String username;
    String postUrl;
    String caption;
    String commentCount;
    String likesCount;
    boolean isLiked;

    public PostModel(){

    };

    public PostModel(String imageurl, String username, String postUrl, String caption, String commentCount, String likesCount, boolean isLiked) {
        this.imageurl = imageurl;
        this.username = username;
        this.postUrl = postUrl;
        this.caption = caption;
        this.commentCount = commentCount;
        this.likesCount = likesCount;
        this.isLiked = isLiked;
    }







    public String getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(String commentCount) {
        this.commentCount = commentCount;
    }

    public String getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(String likesCount) {
        this.likesCount = likesCount;
    }



    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPostUrl() {
        return postUrl;
    }

    public void setPostUrl(String postUrl) {
        this.postUrl = postUrl;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }
}
