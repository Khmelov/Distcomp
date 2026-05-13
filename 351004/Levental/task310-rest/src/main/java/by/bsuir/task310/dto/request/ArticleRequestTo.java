package by.bsuir.task310.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.HashSet;
import java.util.Set;

public class ArticleRequestTo {
    private Long id;

    @NotNull
    private Long creatorId;

    @NotBlank
    @Size(min = 2, max = 256)
    private String title;

    @NotBlank
    @Size(min = 2, max = 4096)
    private String content;

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

    public Set<Long> getStickerIds() {
        return stickerIds;
    }

    public void setStickerIds(Set<Long> stickerIds) {
        this.stickerIds = stickerIds;
    }
}