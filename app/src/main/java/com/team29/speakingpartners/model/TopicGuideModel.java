package com.team29.speakingpartners.model;

import java.util.Date;

public class TopicGuideModel extends Model {

    private String level;
    private String title;
    private String guides;
    private Date created_date;
    private Date modified_date;

    public TopicGuideModel() {
    }

    public TopicGuideModel(String level, String title, String guides, Date created_date, Date modified_date) {
        this.level = level;
        this.title = title;
        this.guides = guides;
        this.created_date = created_date;
        this.modified_date = modified_date;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getGuides() {
        return guides;
    }

    public void setGuides(String guides) {
        this.guides = guides;
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
}
