package com.dc.anotherone.model.dto;

import com.dc.anotherone.model.blo.Marker;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.List;

public class NewsRequestTo {

    private long id;

    private long authorId;

    private String title;

    private String content;

    private Timestamp created;

    private Timestamp modified;

    private List<Marker> markers;

    public NewsRequestTo(long id, long authorId, String title, String content, Timestamp created, Timestamp modified) {
        this.id = id;
        this.authorId = authorId;
        this.title = title;
        this.content = content;
        this.created = created;
        this.modified = modified;
    }

    public NewsRequestTo(long authorId, String title, String content, Timestamp created, Timestamp modified) {
        this.authorId = authorId;
        this.title = title;
        this.content = content;
        this.created = created;
        this.modified = modified;
    }

    public NewsRequestTo(long authorId, String title, String content) {
        this.authorId = authorId;
        this.title = title;
        this.content = content;
    }

    public NewsRequestTo(long authorId, String title, String content, List<Marker> markers) {
        this.authorId = authorId;
        this.title = title;
        this.content = content;
        this.markers = markers;
    }

    private NewsRequestTo() {
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

    public List<Marker> getMarkers() {
        return this.markers;
    }

    public void setMarkers(List<Marker> markers) {
        this.markers = markers;
    }
}
