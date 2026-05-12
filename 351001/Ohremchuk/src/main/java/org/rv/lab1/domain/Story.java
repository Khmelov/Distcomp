package org.rv.lab1.domain;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tbl_story")
public class Story {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "editor_id", nullable = false)
    private Editor editor;

    @Column(nullable = false, length = 64)
    private String title;

    @Column(nullable = false, length = 2048)
    private String content;

    @ManyToMany
    @JoinTable(
            name = "tbl_story_marker",
            joinColumns = @JoinColumn(name = "story_id"),
            inverseJoinColumns = @JoinColumn(name = "marker_id")
    )
    private Set<Marker> markers = new HashSet<>();

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected Story() {
    }

    public Story(Editor editor, String title, String content, Set<Marker> markers, Instant createdAt, Instant updatedAt) {
        this.editor = editor;
        this.title = title;
        this.content = content;
        if (markers != null) {
            this.markers = markers;
        }
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public Editor getEditor() {
        return editor;
    }

    public void setEditor(Editor editor) {
        this.editor = editor;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Set<Marker> getMarkers() {
        return markers;
    }

    public void setMarkers(Set<Marker> markers) {
        this.markers = markers == null ? new HashSet<>() : markers;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}

