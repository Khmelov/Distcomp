package com.example.discussion.kafka;

import com.example.discussion.model.NoticeState;

public class NoticeMessage {

    private Long id;
    private Long newsId;
    private String content;
    private NoticeState state;
    private String correlationId;

    // Конструкторы
    public NoticeMessage() {}

    public NoticeMessage(Long id, Long newsId, String content, NoticeState state, String correlationId) {
        this.id = id;
        this.newsId = newsId;
        this.content = content;
        this.state = state;
        this.correlationId = correlationId;
    }

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getNewsId() { return newsId; }
    public void setNewsId(Long newsId) { this.newsId = newsId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public NoticeState getState() { return state; }
    public void setState(NoticeState state) { this.state = state; }

    public String getCorrelationId() { return correlationId; }
    public void setCorrelationId(String correlationId) { this.correlationId = correlationId; }
}