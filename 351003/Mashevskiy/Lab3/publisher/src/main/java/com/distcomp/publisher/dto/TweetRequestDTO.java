package com.distcomp.publisher.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public class TweetRequestDTO {
    private Long id;

    @NotNull
    private Long creatorId;

    @NotBlank
    @Size(min = 2, max = 64)
    private String title;

    @NotBlank
    @Size(min = 4, max = 2048)
    private String content;

    private List<Long> markerIds;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getCreatorId() { return creatorId; }
    public void setCreatorId(Long creatorId) { this.creatorId = creatorId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public List<Long> getMarkerIds() { return markerIds; }
    public void setMarkerIds(List<Long> markerIds) { this.markerIds = markerIds; }
}