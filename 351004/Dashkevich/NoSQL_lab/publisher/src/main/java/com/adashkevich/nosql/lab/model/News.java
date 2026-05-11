package com.adashkevich.nosql.lab.model;

import jakarta.persistence.*;


import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "tbl_news")
public class News extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "editor_id", nullable = false)
    private Editor editor;

    @Column(nullable = false, unique = true, length = 64)
    private String title;

    @Column(nullable = false, length = 2048)
    private String content;

    @Column(nullable = false)
    private OffsetDateTime created;

    @Column(nullable = false)
    private OffsetDateTime modified;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "tbl_news_marker",
            joinColumns = @JoinColumn(name = "news_id"),
            inverseJoinColumns = @JoinColumn(name = "marker_id")
    )
    private Set<Marker> markers = new HashSet<>();

    public Editor getEditor() { return editor; }
    public void setEditor(Editor editor) { this.editor = editor; }

    public Long getEditorId() { return editor == null ? null : editor.getId(); }
    public void setEditorId(Long ignored) { }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public OffsetDateTime getCreated() { return created; }
    public void setCreated(OffsetDateTime created) { this.created = created; }
    public OffsetDateTime getModified() { return modified; }
    public void setModified(OffsetDateTime modified) { this.modified = modified; }
    public Set<Marker> getMarkers() { return markers; }
    public void setMarkers(Set<Marker> markers) { this.markers = markers == null ? new HashSet<>() : markers; }

    public Set<Long> getMarkerIds() {
        return markers == null ? new HashSet<>() : markers.stream().map(BaseEntity::getId).collect(Collectors.toCollection(HashSet::new));
    }

    public void setMarkerIds(Set<Long> ignored) { }

}
