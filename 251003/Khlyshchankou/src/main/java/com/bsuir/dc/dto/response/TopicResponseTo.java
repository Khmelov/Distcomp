package com.bsuir.dc.dto.response;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TopicResponseTo {
    private long id;
    private long authorId;
    private String title;
    private String content;
    private Date created;
    private Date modified;
    private List<String> labels = new ArrayList<>();

    public void setId(long id) { this.id = id; }
    public long getId() { return id; }

    public void setAuthorId(long authorId) { this.authorId = authorId; }
    public long getAuthorId() { return authorId; }

    public void setTitle(String title) { this.title = title; }
    public String getTitle() { return title; }

    public void setContent(String content) { this.content = content; }
    public String getContent() { return content; }

    public void setCreated(Date created) { this.created = created; }
    public Date getCreated() { return created; }

    public void setModified(Date modified) { this.modified = modified; }
    public Date getModified() { return modified; }

    public void setLabels(List<String> labels) { this.labels = labels; }
    public List<String> getLabels() { return labels; }
}
