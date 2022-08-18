package com.mohitsprojects.instaclone.Models;

import android.net.Uri;

public class PostFb {
    Uri userImage;
    String username;
    Uri post;
    String caption;
    String commentCount;
    String likesCount;

    public PostFb(){

    }


    public PostFb(Uri userImage, String username, Uri post, String caption, String commentCount, String likesCount) {
        this.userImage = userImage;
        this.username = username;
        this.post = post;
        this.caption = caption;
        this.commentCount = commentCount;
        this.likesCount = likesCount;

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



    public Uri getUserImage() {
        return userImage;
    }

    public void setUserImage(Uri userImage) {
        this.userImage = userImage;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Uri getPost() {
        return post;
    }

    public void setPost(Uri post) {
        this.post = post;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }



}
