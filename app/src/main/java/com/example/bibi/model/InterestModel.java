package com.example.bibi.model;

public class InterestModel {
    String imageInterest;
    String followInterest;
    String topic;
    public InterestModel() {
    }

    public InterestModel(String imageInterest, String followInterest) {
        this.imageInterest = imageInterest;
        this.followInterest = followInterest;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getImageInterest() {
        return imageInterest;
    }

    public void setImageInterest(String imageInterest) {
        this.imageInterest = imageInterest;
    }

    public String getFollowInterest() {
        return followInterest;
    }

    public void setFollowInterest(String followInterest) {
        this.followInterest = followInterest;
    }
}
