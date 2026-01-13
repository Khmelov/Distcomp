package com.distcomp.discussion.post.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PostResponse {

    private String country;
    private long articleId;
    private Long id;
    private String content;

    public PostResponse() {
    }

    public PostResponse(String country, long articleId, Long id, String content) {
        this.country = country;
        this.articleId = articleId;
        this.id = id;
        this.content = content;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public long getArticleId() {
        return articleId;
    }

    public void setArticleId(long articleId) {
        this.articleId = articleId;
    }

    @JsonProperty("storyId")
    public long getStoryId() {
        return articleId;
    }

    @JsonProperty("storyId")
    public void setStoryId(long storyId) {
        this.articleId = storyId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
