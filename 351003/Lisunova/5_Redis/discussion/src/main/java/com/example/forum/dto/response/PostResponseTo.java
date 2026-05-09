package com.example.forum.dto.response;

import com.example.forum.entity.PostState;
public class PostResponseTo {

    private Long id;
    private Long topicId;
    private String content;
    private PostState state;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTopicId() {
        return topicId;
    }

    public void setTopicId(Long topicId) {
        this.topicId = topicId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setState(PostState state) { this.state = state; }
}
