package com.example.saffin.androidsmartcity.models;

import android.support.annotation.Nullable;

/**
 * Created by Saffin on 02/05/2020.
 */

public class Message {
    private String mid;
    private String date;
    private String message;
    private String uid;


    public Message() { }

    public Message(String mid,String date, String message, String uid) {
        this.uid = uid;
        this.date = date;
        this.message = message;
        this.mid = mid;

    }
    public Message(String message, String uid) {
        this.uid = uid;
        this.message = message;
    }

    // --- GETTERS ---
    public String getUid() { return uid; }
    public String getMessage(){ return  message;}
    public String getMid() { return  mid;}
    public String getDate() { return  date;}

    // --- SETTERS ---
    public void setUid(String uid) { this.uid = uid; }
    public void setMid(String mid) { this.mid = mid; }
    public void setDate(String date) { this.date = date; }
    public void setMessage(String message){ this.message = message; }
}
