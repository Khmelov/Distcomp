package com.dc.anotherone.model.dto;

public class MessageResponseTo {
    private long id;
    private long newsId;
    private String content;

    public MessageResponseTo(long id, long newsId, String content) {
        this.id = id;
        this.newsId = newsId;
        this.content = content;
    }

    private MessageResponseTo() {
    }

    public long getId() {
        return this.id;
    }

    public long getNewsId() {
        return this.newsId;
    }

    public String getContent() {
        return this.content;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setNewsId(long newsId) {
        this.newsId = newsId;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
