package com.distcomp.publisher.article.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Set;

public class ArticleRequest {

    @NotNull
    private Long writerId;

    @NotBlank
    @Size(max = 64)
    private String title;

    @NotBlank
    @Size(max = 2048)
    private String content;

    private Set<Long> stickerIds;

    public ArticleRequest() {
    }

    public Long getWriterId() {
        return writerId;
    }

    public void setWriterId(Long writerId) {
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

    public Set<Long> getStickerIds() {
        return stickerIds;
    }

    public void setStickerIds(Set<Long> stickerIds) {
        this.stickerIds = stickerIds;
    }
}
