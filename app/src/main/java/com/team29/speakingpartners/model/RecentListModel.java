package com.team29.speakingpartners.model;

import java.util.Date;

public class RecentListModel {

    private String user_name;
    private String record_title;
    private String call_type;
    private Date record_date;
    private String record_duration;
    private String toEmail;
    private String fromEmail;
    private String record_file_url;

    public RecentListModel() {
    }

    public RecentListModel(String user_name, String record_title, String call_type,
                           Date record_date, String record_duration, String toEmail,
                           String fromEmail, String record_file_url) {
        this.user_name = user_name;
        this.record_title = record_title;
        this.call_type = call_type;
        this.record_date = record_date;
        this.record_duration = record_duration;
        this.toEmail = toEmail;
        this.fromEmail = fromEmail;
        this.record_file_url = record_file_url;
    }

    public RecentListModel(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getRecord_title() {
        return record_title;
    }

    public void setRecord_title(String record_title) {
        this.record_title = record_title;
    }

    public String getCall_type() {
        return call_type;
    }

    public void setCall_type(String call_type) {
        this.call_type = call_type;
    }

    public Date getRecord_date() {
        return record_date;
    }

    public void setRecord_date(Date record_date) {
        this.record_date = record_date;
    }

    public String getRecord_duration() {
        return record_duration;
    }

    public void setRecord_duration(String record_duration) {
        this.record_duration = record_duration;
    }

    public String getToEmail() {
        return toEmail;
    }

    public void setToEmail(String toEmail) {
        this.toEmail = toEmail;
    }

    public String getFromEmail() {
        return fromEmail;
    }

    public void setFromEmail(String fromEmail) {
        this.fromEmail = fromEmail;
    }

    public String getRecord_file_url() {
        return record_file_url;
    }

    public void setRecord_file_url(String record_file_url) {
        this.record_file_url = record_file_url;
    }
}
