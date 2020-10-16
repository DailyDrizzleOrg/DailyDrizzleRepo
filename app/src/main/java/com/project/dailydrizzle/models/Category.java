package com.project.dailydrizzle.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Category implements Parcelable {

    private String id, name;
    private ArrayList<Video> videoArrayList;

    public Category(String id, String name, ArrayList<Video> videoArrayList) {
        this.id = id;
        this.name = name;
        this.videoArrayList = videoArrayList;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Video> getVideoArrayList() {
        return videoArrayList;
    }

    public void setVideoArrayList(ArrayList<Video> videoArrayList) {
        this.videoArrayList = videoArrayList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}
