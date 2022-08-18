package com.mohitsprojects.instaclone.Models;

public class ImageModel {
    private String imageUrl;

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public ImageModel(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
