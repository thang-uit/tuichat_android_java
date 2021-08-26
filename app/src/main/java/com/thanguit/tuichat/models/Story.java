package com.thanguit.tuichat.models;

public class Story {
    private String image;
    private String time;

    public Story() {
    }

    public Story(String image, String time) {
        this.image = image;
        this.time = time;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
