package com.example.saffin.androidsmartcity.models;

/**
 * Created by Saffin on 02/05/2020.
 */

public class Group {
    private String gid;
    private String name;

    public Group() { }

    public Group(String gid,String name) {
        this.gid = gid;
        this.name = name;
    }


    // --- GETTERS ---
    public String getGid(){ return gid;}
    public String getName(){ return name;}

    // --- SETTERS ---
    public void setGid(String gid) { this.gid = gid; }
    public void setName(String name){ this.name = name; }
}