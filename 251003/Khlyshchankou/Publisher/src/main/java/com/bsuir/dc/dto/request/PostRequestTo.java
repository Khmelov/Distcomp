package com.bsuir.dc.dto.request;

import jakarta.validation.constraints.Size;

public class PostRequestTo {
    private long id;
    private long topicId;

    @Size(min = 2, max = 2048, message = "Content size must be between 2..64 characters")
    private String content;

    public void setId(long id) { this.id = id; }
    public Long getId() { return id; }

    public void setTopicId(long topicId) { this.topicId = topicId; }
    public long getTopicId() { return topicId; }

    public void setContent(String content) { this.content = content; }
    public String getContent() { return content; }

    public PostRequestTo(Long id) { this.id = id; }

    public PostRequestTo() {}
}
