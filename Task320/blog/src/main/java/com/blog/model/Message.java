package com.blog.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "tbl_message", schema = "distcomp")
public class Message extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id", nullable = false)
    private Topic topic;

    @Column(name = "content", nullable = false, length = 2048)
    @Size(min = 4, max = 2048)
    private String content;

    // Конструкторы
    public Message() {
        super();
    }

    public Message(Topic topic, String content) {
        this.topic = topic;
        this.content = content;
    }

    // Геттеры и сеттеры
    public Topic getTopic() {
        return topic;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}