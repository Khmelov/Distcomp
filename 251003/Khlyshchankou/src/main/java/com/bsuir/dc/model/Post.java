package com.bsuir.dc.model;

import jakarta.persistence.*;

@Entity
@Table(name = "tbl_post")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @ManyToOne
    @JoinColumn(name = "topicId", referencedColumnName = "id")
    private Topic topic;

    @Column(name = "content")
    private String content;

    public void setId(long id) { this.id = id; }
    public long getId() { return id; }

    public void setTopic(Topic topic) { this.topic = topic; }
    public Topic getTopic() { return topic; }

    public void setContent(String content) { this.content = content; }
    public String getContent() { return content; }
}
