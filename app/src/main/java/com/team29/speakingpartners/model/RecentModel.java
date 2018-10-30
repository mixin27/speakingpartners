package com.team29.speakingpartners.model;

import java.util.List;

public class RecentModel {

    private String email;
    private List<RecentModel> record_lists;

    public RecentModel() {
    }

    public RecentModel(String email, List<RecentModel> record_lists) {
        this.email = email;
        this.record_lists = record_lists;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<RecentModel> getRecord_lists() {
        return record_lists;
    }

    public void setRecord_lists(List<RecentModel> record_lists) {
        this.record_lists = record_lists;
    }
}
