package com.bsuir.dc.dto.response;

public class PostResponseTo {
    private long id;
    private long topicId;
    private String content;

    public void setId(long id) { this.id = id; }
    public long getId() { return id; }

    public void setTopicId(long topicId) { this.topicId = topicId; }
    public long getTopicId() { return topicId; }

    public void setContent(String content) { this.content = content; }
    public String getContent() { return content; }
}
