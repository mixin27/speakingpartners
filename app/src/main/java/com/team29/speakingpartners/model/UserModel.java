package com.team29.speakingpartners.model;

import java.util.Date;

public class UserModel {

    private String user_name;
    private String email;
    private String password;
    private String gender;
    private String level;
    private String country;
    private String url_photo;
    private Date created_date;
    private Date modified_date;
    private Date date_of_birth;
    private int active_status;

    public UserModel() {
    }

    public UserModel(String user_name, String email, String gender, String level,
                     String country, String url_photo, Date created_date,
                     Date modified_date, Date date_of_birth, int active_status) {
        this.user_name = user_name;
        this.email = email;
        this.gender = gender;
        this.level = level;
        this.country = country;
        this.url_photo = url_photo;
        this.created_date = created_date;
        this.modified_date = modified_date;
        this.date_of_birth = date_of_birth;
        this.active_status = active_status;
    }

    public UserModel(String user_name, String email,
                     String password, String gender, String level, String country,
                     String url_photo, Date created_date, Date modified_date,
                     Date date_of_birth, int active_status) {
        this.user_name = user_name;
        this.email = email;
        this.password = password;
        this.gender = gender;
        this.level = level;
        this.country = country;
        this.url_photo = url_photo;
        this.created_date = created_date;
        this.modified_date = modified_date;
        this.date_of_birth = date_of_birth;
        this.active_status = active_status;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getUrl_photo() {
        return url_photo;
    }

    public void setUrl_photo(String url_photo) {
        this.url_photo = url_photo;
    }

    public Date getCreated_date() {
        return created_date;
    }

    public void setCreated_date(Date created_date) {
        this.created_date = created_date;
    }

    public Date getModified_date() {
        return modified_date;
    }

    public void setModified_date(Date modified_date) {
        this.modified_date = modified_date;
    }

    public Date getDate_of_birth() {
        return date_of_birth;
    }

    public void setDate_of_birth(Date date_of_birth) {
        this.date_of_birth = date_of_birth;
    }

    public int getActive_status() {
        return active_status;
    }

    public void setActive_status(int active_status) {
        this.active_status = active_status;
    }
}
