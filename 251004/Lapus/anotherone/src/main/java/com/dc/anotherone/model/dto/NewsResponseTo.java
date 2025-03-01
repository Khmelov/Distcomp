package com.dc.anotherone.model.dto;

import java.sql.Timestamp;

public class NewsResponseTo {

    private long id;

    private long authorId;

    private String title;

    private String content;

    private Timestamp created;

    private Timestamp modified;

    public NewsResponseTo(long id, long authorId, String title, String content, Timestamp created, Timestamp modified) {
        this.id = id;
        this.authorId = authorId;
        this.title = title;
        this.content = content;
        this.created = created;
        this.modified = modified;
    }

    private NewsResponseTo() {
    }

    public long getId() {
        return this.id;
    }

    public long getAuthorId() {
        return this.authorId;
    }

    public String getTitle() {
        return this.title;
    }

    public String getContent() {
        return this.content;
    }

    public Timestamp getCreated() {
        return this.created;
    }

    public Timestamp getModified() {
        return this.modified;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setAuthorId(long authorId) {
        this.authorId = authorId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setCreated(Timestamp created) {
        this.created = created;
    }

    public void setModified(Timestamp modified) {
        this.modified = modified;
    }
}
