package com.team29.speakingpartners.model;

public class Country extends Model {

    private String name;
    private String short_notation;
    private String flag_url;

    public Country(String name) {
        this.name = name;
    }

    public Country(String name, String short_notation, String flag_url) {
        this.name = name;
        this.short_notation = short_notation;
        this.flag_url = flag_url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShort_notation() {
        return short_notation;
    }

    public void setShort_notation(String short_notation) {
        this.short_notation = short_notation;
    }

    public String getFlag_url() {
        return flag_url;
    }

    public void setFlag_url(String flag_url) {
        this.flag_url = flag_url;
    }
}
