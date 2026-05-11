package by.bsuir.task310.dto.response;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

public class ArticleResponseTo {
    private Long id;
    private Long creatorId;
    private String title;
    private String content;
    private Instant created;
    private Instant modified;
    private Set<Long> stickerIds = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public Set<Long> getStickerIds() {
        return stickerIds;
    }

    public void setStickerIds(Set<Long> stickerIds) {
        this.stickerIds = stickerIds;
    }
}