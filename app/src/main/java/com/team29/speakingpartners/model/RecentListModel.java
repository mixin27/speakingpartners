package com.team29.speakingpartners.model;

import android.annotation.SuppressLint;

import com.google.firebase.firestore.IgnoreExtraProperties;

import java.text.SimpleDateFormat;
import java.util.Date;

public class RecentListModel extends Model {

    private String channel_id;
    private String req_topic;
    private String from_email;
    private String to_email;
    private Date date_time;

    public RecentListModel() {
    }

    public RecentListModel(String channel_id, String req_topic, String from_email, String to_email, Date date_time) {
        this.channel_id = channel_id;
        this.req_topic = req_topic;
        this.from_email = from_email;
        this.to_email = to_email;
        this.date_time = date_time;
    }

    public String getChannel_id() {
        return channel_id;
    }

    public void setChannel_id(String channel_id) {
        this.channel_id = channel_id;
    }

    public String getReq_topic() {
        return req_topic;
    }

    public void setReq_topic(String req_topic) {
        this.req_topic = req_topic;
    }

    public String getFrom_email() {
        return from_email;
    }

    public void setFrom_email(String from_email) {
        this.from_email = from_email;
    }

    public String getTo_email() {
        return to_email;
    }

    public void setTo_email(String to_email) {
        this.to_email = to_email;
    }

    public Date getDate_time() {
        return date_time;
    }

    public void setDate_time(Date date_time) {
        this.date_time = date_time;
    }

    public String getDateString() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return sdf.format(getDate_time());
    }

    public String getTimeString() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
        return sdf.format(getDate_time());
    }

    public String getDateTimeString() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
        return sdf.format(getDate_time());
    }
}
