package com.example.discussion.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NoticeMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long newsId;
    private String content;
    private String state;
    private String correlationId;

    public NoticeMessage() {}

    public NoticeMessage(Long id, Long newsId, String content, String state, String correlationId) {
        this.id = id;
        this.newsId = newsId;
        this.content = content;
        this.state = state;
        this.correlationId = correlationId;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getNewsId() { return newsId; }
    public void setNewsId(Long newsId) { this.newsId = newsId; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    public String getCorrelationId() { return correlationId; }
    public void setCorrelationId(String correlationId) { this.correlationId = correlationId; }

    @Override
    public String toString() {
        return "NoticeMessage{" +
                "id=" + id +
                ", newsId=" + newsId +
                ", content='" + content + '\'' +
                ", state='" + state + '\'' +
                ", correlationId='" + correlationId + '\'' +
                '}';
    }
}