package com.distcomp.publisher.article.dto;

import java.time.OffsetDateTime;
import java.util.Set;

public class ArticleResponse {

    private long id;
    private long writerId;
    private String title;
    private String content;
    private OffsetDateTime created;
    private OffsetDateTime modified;
    private Set<Long> stickerIds;

    public ArticleResponse() {
    }

    public ArticleResponse(long id, long writerId, String title, String content, OffsetDateTime created, OffsetDateTime modified, Set<Long> stickerIds) {
        this.id = id;
        this.writerId = writerId;
        this.title = title;
        this.content = content;
        this.created = created;
        this.modified = modified;
        this.stickerIds = stickerIds;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getWriterId() {
        return writerId;
    }

    public void setWriterId(long writerId) {
        this.writerId = writerId;
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

    public OffsetDateTime getCreated() {
        return created;
    }

    public void setCreated(OffsetDateTime created) {
        this.created = created;
    }

    public OffsetDateTime getModified() {
        return modified;
    }

    public void setModified(OffsetDateTime modified) {
        this.modified = modified;
    }

    public Set<Long> getStickerIds() {
        return stickerIds;
    }

    public void setStickerIds(Set<Long> stickerIds) {
        this.stickerIds = stickerIds;
    }
}
