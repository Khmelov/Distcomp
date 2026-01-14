package org.example;

import java.time.OffsetDateTime;
import java.util.List;

public class StoryResponseTo {

    private Long id;
    private String title;
    private String content;
    private OffsetDateTime created;
    private OffsetDateTime modified;
    private Long writerId;
    private List<TagShortResponseTo> tags;

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public OffsetDateTime getCreated() {
        return created;
    }

    public OffsetDateTime getModified() {
        return modified;
    }

    public Long getWriterId() {
        return writerId;
    }

    public List<TagShortResponseTo> getTags() {
        return tags;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setCreated(OffsetDateTime created) {
        this.created = created;
    }

    public void setModified(OffsetDateTime modified) {
        this.modified = modified;
    }

    public void setWriterId(Long writerId) {
        this.writerId = writerId;
    }

    public void setTags(List<TagShortResponseTo> tags) {
        this.tags = tags;
    }
}