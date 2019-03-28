package com.ibrhmdurna.chatapp.Models;

import java.util.Map;

public class Account {

    private String uid;
    private String email;
    private String name;
    private String surname;
    private String phone;
    private String birthday;
    private String gender;
    private String location;
    private String profile_image;
    private String thumb_image;
    private boolean online;
    private Map<String, String> last_seen;

    public Account() {
    }

    public Account(String email, String name, String surname, String phone, String birthday, String gender, String location, String profile_image, String thumb_image, boolean online, Map<String, String> last_seen) {
        this.email = email;
        this.name = name;
        this.surname = surname;
        this.phone = phone;
        this.birthday = birthday;
        this.gender = gender;
        this.location = location;
        this.profile_image = profile_image;
        this.thumb_image = thumb_image;
        this.online = online;
        this.last_seen = last_seen;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getProfile_image() {
        return profile_image;
    }

    public void setProfile_image(String profile_image) {
        this.profile_image = profile_image;
    }

    public String getThumb_image() {
        return thumb_image;
    }

    public void setThumb_image(String thumb_image) {
        this.thumb_image = thumb_image;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public Map<String, String> getLast_seen() {
        return last_seen;
    }

    public void setLast_seen(Map<String, String> last_seen) {
        this.last_seen = last_seen;
    }

    public String getNameSurname(){
        return name + " " + surname;
    }
}
