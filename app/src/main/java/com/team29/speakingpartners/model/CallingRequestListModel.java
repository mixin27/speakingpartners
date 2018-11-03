package com.team29.speakingpartners.model;

import java.util.Date;

public class CallingRequestListModel {

    private String channel_id;
    private boolean from_status;
    private boolean to_status;

    private String from_email;
    private String to_email;
    private String req_topic;

    private Date date;

    public CallingRequestListModel() {
    }

    public CallingRequestListModel(String channel_id, boolean from_status, boolean to_status) {
        this.channel_id = channel_id;
        this.from_status = from_status;
        this.to_status = to_status;
    }

    public CallingRequestListModel(String channel_id, boolean from_status,
                                   boolean to_status, String from_email, String to_email,
                                   Date date, String req_topic) {
        this.channel_id = channel_id;
        this.from_status = from_status;
        this.to_status = to_status;
        this.from_email = from_email;
        this.to_email = to_email;
        this.date = date;
        this.req_topic = req_topic;
    }

    public String getChannel_id() {
        return channel_id;
    }

    public void setChannel_id(String channel_id) {
        this.channel_id = channel_id;
    }

    public boolean isFrom_status() {
        return from_status;
    }

    public void setFrom_status(boolean from_status) {
        this.from_status = from_status;
    }

    public boolean isTo_status() {
        return to_status;
    }

    public void setTo_status(boolean to_status) {
        this.to_status = to_status;
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getReq_topic() {
        return req_topic;
    }

    public void setReq_topic(String req_topic) {
        this.req_topic = req_topic;
    }
}
