package com.task.rest.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tweets")
public class Tweet extends BaseEntity {
    @NotNull(message = "Writer ID cannot be null")
    private Long writerId;

    @NotBlank(message = "Title cannot be blank")
    @Size(max = 64, message = "Title must be at most 64 characters")
    private String title;

    @NotBlank(message = "Content cannot be blank")
    @Size(max = 2048, message = "Content must be at most 2048 characters")
    private String content;

    @Column(name = "created", nullable = false, updatable = false)
    private LocalDateTime created = LocalDateTime.now();

    @Column(name = "modified", nullable = false)
    private LocalDateTime modified = LocalDateTime.now();

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "tweet_marks",
            joinColumns = @JoinColumn(name = "tweet_id"),
            inverseJoinColumns = @JoinColumn(name = "mark_id")
    )
    private List<Mark> marks = new ArrayList<>();

    public Tweet() {}

    public Tweet(Long writerId, String title, String content) {
        this.writerId = writerId;
        this.title = title;
        this.content = content;
    }

    public Long getWriterId() { return writerId; }
    public void setWriterId(Long writerId) { this.writerId = writerId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public LocalDateTime getCreated() { return created; }
    public void setCreated(LocalDateTime created) { this.created = created; }
    public LocalDateTime getModified() { return modified; }
    public void setModified(LocalDateTime modified) { this.modified = modified; }
    public List<Mark> getMarks() { return marks; }
    public void setMarks(List<Mark> marks) { this.marks = marks; }
    public void addMark(Mark mark) {
        this.marks.add(mark);
        mark.getTweets().add(this);
    }
    public void removeMark(Mark mark) {
        this.marks.remove(mark);
        mark.getTweets().remove(this);
    }
}