package com.example.bibi.model;

import com.google.firebase.Timestamp;

import java.util.List;
import java.util.Map;

public class PostsModel {
    String postImage;
    String uid;
    String nameUser;
    String title;
    String postId;
    String imageUser;
    Map<String, Float> labelMap; // Thêm trường này để lưu trữ thông tin nhãn và mức độ phần trăm

    List<String> tags;
    long timestamp;
    private float interestLevel;

    public float getInterestLevel() {
        return interestLevel;
    }

    public void setInterestLevel(float interestLevel) {
        this.interestLevel = interestLevel;
    }
    public PostsModel() {
    }

    public PostsModel(String title, String postImage, String uid,long timestamp,Map<String, Float> labelMap) {
        this.postImage = postImage;
        this.uid = uid;
        this.title = title;
        this.timestamp = timestamp;
        this.labelMap = labelMap;
    }
    public PostsModel(String title, String postImage, String uid,long timestamp,String postId) {
        this.postImage = postImage;
        this.uid = uid;
        this.title = title;
        this.timestamp = timestamp;
        this.postId = postId;
    }
    public PostsModel(String postImage, String uid, String nameUser, String title) {
        this.postImage = postImage;
        this.uid = uid;
        this.nameUser = nameUser;
        this.title = title;
    }
    public Map<String, Float> getLabelMap() {
        return labelMap;
    }

    public Float getConfidence(String label) {
        if (labelMap.containsKey(label)) {
            return labelMap.get(label);
        } else {
            return 0.0f; // Hoặc giá trị mặc định phù hợp
        }
    }

    public String getImageUser() {
        return imageUser;
    }

    public void setImageUser(String imageUser) {
        this.imageUser = imageUser;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public boolean isMatching(Map<String, Float> selectedLabelMap) {
        for (Map.Entry<String, Float> entry : selectedLabelMap.entrySet()) {
            String label = entry.getKey();
            Float selectedConfidence = entry.getValue();
            Float currentConfidence = getConfidence(label);

            // So sánh phần trăm
            if (currentConfidence < selectedConfidence) {
                return false;
            }
        }
        return true;
    }

    public Map<String, Float> getLabels() {
        return labelMap;
    }

    public void setLabels(Map<String, Float> labels) {
        this.labelMap = labels;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getPostImage() {
        return postImage;
    }

    public void setPostImage(String postImage) {
        this.postImage = postImage;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getNameUser() {
        return nameUser;
    }

    public void setNameUser(String nameUser) {
        this.nameUser = nameUser;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
