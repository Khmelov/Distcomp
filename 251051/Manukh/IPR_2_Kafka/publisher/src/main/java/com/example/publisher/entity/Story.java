package com.example.publisher.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tbl_story")
public class Story extends BaseEntity {

    @Column(name = "title", nullable = false, length = 64)
    private String title;

    @Column(name = "content", nullable = false, length = 2048)
    private String content;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "modified_at", nullable = false)
    private LocalDateTime modifiedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "editor_id", nullable = false)
    private Editor editor;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "tbl_story_mark",
            schema = "distcomp",
            joinColumns = @JoinColumn(name = "story_id"),
            inverseJoinColumns = @JoinColumn(name = "mark_id")
    )
    private Set<Mark> marks = new HashSet<>();

    @OneToMany(mappedBy = "story", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Reaction> reactions = new HashSet<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        modifiedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        modifiedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getModifiedAt() { return modifiedAt; }
    public void setModifiedAt(LocalDateTime modifiedAt) { this.modifiedAt = modifiedAt; }

    public Editor getEditor() { return editor; }
    public void setEditor(Editor editor) { this.editor = editor; }

    public Set<Mark> getMarks() { return marks; }
    public void setMarks(Set<Mark> marks) { this.marks = marks; }

    public Set<Reaction> getReactions() { return reactions; }
    public void setReactions(Set<Reaction> reactions) { this.reactions = reactions; }

    // Helper methods
    public void addMark(Mark mark) {
        this.marks.add(mark);
        mark.getStories().add(this);
    }

    public void removeMark(Mark mark) {
        this.marks.remove(mark);
        mark.getStories().remove(this);
    }
}