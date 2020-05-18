package com.example.saffin.androidsmartcity.models;

import android.support.annotation.Nullable;

/**
 * Created by Saffin on 29/04/2020.
 */

public class User {
    private String uid;
    private String username;
    private String firstName;
    private String secondName;
    private String age;
    @Nullable
    private String urlPicture;

    public User() { }

    public User(String uid, String username,String firstName, String secondName, String urlPicture, String age) {
        this.uid = uid;
        this.username = username;
        this.urlPicture = urlPicture;
        this.firstName = firstName;
        this.secondName = secondName;
        this.age = age;

    }

    // --- GETTERS ---
    public String getUid() { return uid; }
    public String getUsername() { return username; }
    public String getUrlPicture() { return urlPicture; }
    public String getFirstName() { return firstName; }
    public String getSecondName() { return secondName; }
    public String getAge() { return age; }

    // --- SETTERS ---
    public void setUsername(String username) { this.username = username; }
    public void setUid(String uid) { this.uid = uid; }
    public void setUrlPicture(String urlPicture) { this.urlPicture = urlPicture; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setSecondName(String secondName) { this.secondName = secondName; }
    public void setAge(String age) { this.age = age; }
}
