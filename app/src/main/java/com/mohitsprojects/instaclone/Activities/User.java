package com.mohitsprojects.instaclone.Activities;

public class User {
    public String name = "",userName = "",email = "",bio = "";
    public String profileImage="";
    public String Id = "";

    public User(){

    }

    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public User(String name, String userName, String email, String bio, String image) {
        this.name = name;
        this.userName = userName;
        this.email = email;
        this.bio = bio;
        profileImage = image;
    }
}
