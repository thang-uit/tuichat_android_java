package com.thanguit.tuichat.models;

import java.util.List;

public class UserStory {
    private String name;
    private String avatar;
    private String lastUpdated;
    private List<Story> storyList;

    public UserStory() {
    }

    public UserStory(String name, String avatar, String lastUpdated, List<Story> storyList) {
        this.name = name;
        this.avatar = avatar;
        this.lastUpdated = lastUpdated;
        this.storyList = storyList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public List<Story> getStoryList() {
        return storyList;
    }

    public void setStoryList(List<Story> storyList) {
        this.storyList = storyList;
    }
}
