package com.adashkevich.rest.lab.model;

public class Message extends BaseEntity {
    private Long newsId;
    private String content;

    public Long getNewsId() { return newsId; }
    public void setNewsId(Long newsId) { this.newsId = newsId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
