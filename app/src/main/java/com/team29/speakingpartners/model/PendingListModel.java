package com.team29.speakingpartners.model;

public class PendingListModel {

    private String user_name;
    private String level;

    public PendingListModel(String user_name, String level) {
        this.user_name = user_name;
        this.level = level;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }
}
