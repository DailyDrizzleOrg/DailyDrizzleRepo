package com.project.dailydrizzle.models;

public class Video {

    private String id, title, desc, videoUrl, categoryId,category,thumbnail;
    boolean isPlaying;



    public Video(String id, String title, String desc, String videoUrl, String categoryId, boolean isPlaying,String category,String thumbnail) {
        this.id = id;
        this.title = title;
        this.desc = desc;
        this.videoUrl = videoUrl;
        this.categoryId = categoryId;
        this.isPlaying = isPlaying;
        this.category=category;
        this.thumbnail=thumbnail;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }
}