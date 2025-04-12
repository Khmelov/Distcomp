package com.example.rest.dto.post;

import com.example.rest.entity.Topic;
import jakarta.validation.constraints.Size;
import lombok.Data;

public class PostRequestTo {
    @Size(min = 2, max = 2048, message = "Content must be between 2 and 2048 characters")
    private String content;
    private Long topicId;



    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getTopicId() {
        return topicId;
    }

    public void setTopicId(Long topicId) {
        this.topicId = topicId;
    }
}
