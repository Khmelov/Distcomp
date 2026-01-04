package com.example.publisher.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;

@Entity
@Table(name = "tbl_comment")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "story_id", nullable = false)
    private Story story;

    @NotBlank
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    private Instant created = Instant.now();

    public Comment() {}
    public Comment(Story story, String content) {
        this.story = story;
        this.content = content;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Story getStory() { return story; }
    public void setStory(Story story) { this.story = story; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Instant getCreated() { return created; }
    public void setCreated(Instant created) { this.created = created; }
}