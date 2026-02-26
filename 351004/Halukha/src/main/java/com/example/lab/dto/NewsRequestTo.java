package com.example.lab.dto;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class NewsRequestTo {

    private Long userId;

    @NotBlank
    @Size(min = 2, max = 64)
    private String title;

    @NotBlank
    @Size(min = 4, max = 2048)
    private String content;

    private List<String> markers;

    @DateTimeFormat(iso = ISO.DATE_TIME)
    private LocalDateTime created;

    @DateTimeFormat(iso = ISO.DATE_TIME)
    private LocalDateTime modified;

    public NewsRequestTo(
            @JsonProperty("userId") Long userId,
            @JsonProperty("title") String title,
            @JsonProperty("content") String content,
            @JsonProperty("markers") List<String> markers,
            @JsonProperty("created") LocalDateTime created,
            @JsonProperty("modified") LocalDateTime modified) {
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.markers = markers;
        this.created = created;
        this.modified = modified;
    }

    public Long getUserId() {
        return userId;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public List<String> getMarkers() {
        return markers;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public LocalDateTime getModified() {
        return modified;
    }
}
