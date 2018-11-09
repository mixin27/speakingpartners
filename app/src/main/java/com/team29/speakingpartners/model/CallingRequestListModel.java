package com.team29.speakingpartners.model;

import android.annotation.SuppressLint;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CallingRequestListModel extends Model implements Serializable {

    private String channel_id;
    private String from_email;
    private String to_email;
    private int from_status;
    private int to_status; /* 0 = null, 1 = accept, 2 = reject */
    private int call_type; /* 0 = null, 1 = direct, 2 = request */
    private String req_topic;
    private Date date;

    public CallingRequestListModel() {
    }

    public CallingRequestListModel(String channel_id, String from_email, String to_email,
                                   int from_status, int to_status, int call_type, String req_topic, Date date) {
        this.channel_id = channel_id;
        this.from_email = from_email;
        this.to_email = to_email;
        this.from_status = from_status;
        this.to_status = to_status;
        this.call_type = call_type;
        this.req_topic = req_topic;
        this.date = date;
    }

    public String getChannel_id() {
        return channel_id;
    }

    public void setChannel_id(String channel_id) {
        this.channel_id = channel_id;
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

    public int getFrom_status() {
        return from_status;
    }

    public void setFrom_status(int from_status) {
        this.from_status = from_status;
    }

    public int getTo_status() {
        return to_status;
    }

    public void setTo_status(int to_status) {
        this.to_status = to_status;
    }

    public int getCall_type() {
        return call_type;
    }

    public void setCall_type(int call_type) {
        this.call_type = call_type;
    }

    public String getReq_topic() {
        return req_topic;
    }

    public void setReq_topic(String req_topic) {
        this.req_topic = req_topic;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getDateString() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return sdf.format(getDate());
    }

    public String getTimeString() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
        return sdf.format(getDate());
    }

    public String getDateTimeString() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
        return sdf.format(getDate());
    }
}
