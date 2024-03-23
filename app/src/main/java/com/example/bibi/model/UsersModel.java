package com.example.bibi.model;

import com.google.firebase.Timestamp;

import java.util.List;

public class UsersModel {
    String name;
    Timestamp birthday;
    List<String> hobble;
    String password;
    String profileImage;
    String email;
    String phone;
    String sex;
    String uid;
    Timestamp createdTimestamp;
    String coverImage;
    public UsersModel() {
    }

    public UsersModel(String password, String email) {
        this.password = password;
        this.email = email;
    }

    public UsersModel(String name, Timestamp birthday, List<String> hobble, String password, String phone, Timestamp createdTimestamp) {
        this.name = name;
        this.birthday = birthday;
        this.hobble = hobble;
        this.password = password;
        this.phone = phone;
        this.createdTimestamp = createdTimestamp;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Timestamp getBirthday() {
        return birthday;
    }

    public void setBirthday(Timestamp birthday) {
        this.birthday = birthday;
    }

    public List<String> getHobble() {
        return hobble;
    }

    public void setHobble(List<String> hobble) {
        this.hobble = hobble;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Timestamp getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(Timestamp createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }
}
