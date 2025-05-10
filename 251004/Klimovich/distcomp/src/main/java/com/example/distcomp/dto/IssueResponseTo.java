package com.example.distcomp.dto;

import lombok.Data;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
@Data
public class IssueResponseTo {
    private long id;

    private String title;

    private String content;

    private Timestamp created;

    private Timestamp modified;

    private List<String> labels = new ArrayList<>();
    private long writerId;

    public IssueResponseTo() {
    }

    public IssueResponseTo(long id, String title, String content, Timestamp created, Timestamp modified, List<String> labels, long writerId){
        this.id = id;
        this.title = title;
        this.content = content;
        this.created = created;
        this.modified = modified;
        this.labels = labels;
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Timestamp getCreated() {
        return created;
    }

    public void setCreated(Timestamp created) {
        this.created = created;
    }

    public Timestamp getModified() {
        return modified;
    }

    public void setModified(Timestamp modified) {
        this.modified = modified;
    }

    public long getWriterId() {
        return writerId;
    }

    public void setWriterId(long writerId) {
        this.writerId = writerId;
    }

    public List<String> getLabels() {
        return labels;
    }

    public void setLabels(List<String> labels) {
        this.labels = labels;
    }
}

