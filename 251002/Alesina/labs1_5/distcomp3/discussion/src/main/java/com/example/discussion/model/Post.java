package com.example.discussion.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection  = "tbl_posts")
public class Post {

    public Post(){}

    public Post(String content, Long topicId){
        this.content = content;
        this.topicId = topicId;
    }
    @Id
    private Long id;

    private Long topicId;

    private String content;

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}