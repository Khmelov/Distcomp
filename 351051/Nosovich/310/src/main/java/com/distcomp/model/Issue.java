package com.distcomp.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * A problem statement (the "many" side of Creator-Issue, the "one" side of
 * Issue-Notice, and one side of the Issue-Tag many-to-many relation).
 */
public class Issue extends AbstractEntity {

    private Long creatorId;

    private String title;

    private String content;

    private Instant created;

    private Instant modified;

    /**
     * Identifiers of the related tags. Kept as a plain collection because the
     * storage layer has no relation mapping of its own yet.
     */
    private List<Long> tagIds = new ArrayList<>();

    public Long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
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

    public Instant getCreated() {
        return created;
    }

    public void setCreated(Instant created) {
        this.created = created;
    }

    public Instant getModified() {
        return modified;
    }

    public void setModified(Instant modified) {
        this.modified = modified;
    }

    public List<Long> getTagIds() {
        return tagIds;
    }

    public void setTagIds(List<Long> tagIds) {
        this.tagIds = tagIds;
    }

    @Override
    public String toString() {
        return "Issue{id=" + getId() + ", title='" + title + "'}";
    }
}
