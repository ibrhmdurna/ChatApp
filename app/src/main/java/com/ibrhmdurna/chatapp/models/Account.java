package com.ibrhmdurna.chatapp.models;

public class Account {

    private String uid;
    private String email;
    private String name;
    private String surname;
    private String phone;
    private String birthday;
    private int gender;
    private int location;
    private String profile_image;
    private String thumb_image;
    private String search_name;
    private boolean online;
    private Long lastSeen;

    private String convertGender;
    private String convertLocation;

    public Account() {
    }

    public Account(String email, String name, String surname, String phone, String birthday, int gender, int location, String profile_image, String thumb_image, String search_name, boolean online, Long lastSeen) {
        this.email = email;
        this.name = name;
        this.surname = surname;
        this.phone = phone;
        this.birthday = birthday;
        this.gender = gender;
        this.location = location;
        this.profile_image = profile_image;
        this.thumb_image = thumb_image;
        this.search_name = search_name;
        this.online = online;
        this.lastSeen = lastSeen;
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

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public int getLocation() {
        return location;
    }

    public void setLocation(int location) {
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

    public String getSearch_name() {
        return search_name;
    }

    public void setSearch_name(String search_name) {
        this.search_name = search_name;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public Long getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(Long lastSeen) {
        this.lastSeen = lastSeen;
    }

    public String getConvertGender() {
        return convertGender;
    }

    public void setConvertGender(String convertGender) {
        this.convertGender = convertGender;
    }

    public String getConvertLocation() {
        return convertLocation;
    }

    public void setConvertLocation(String convertLocation) {
        this.convertLocation = convertLocation;
    }

    public String getNameSurname(){
        return name + " " + surname;
    }
}
