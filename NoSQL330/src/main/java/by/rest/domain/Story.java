package by.rest.domain;

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

    @ManyToOne(optional = false)
    @JoinColumn(name = "editor_id", nullable = false)
    private Editor editor;

    @Column(nullable = false, length = 64)
    private String title;

    @Column(nullable = false, columnDefinition = "text")
    private String content;

    @ManyToMany
    @JoinTable(
        name = "tbl_story_tag",
        joinColumns = @JoinColumn(name = "story_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new HashSet<>();

    @Column(name = "created")
    private Instant created;

    @Column(name = "modified")
    private Instant modified;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Editor getEditor() { return editor; }
    public void setEditor(Editor editor) { this.editor = editor; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public Set<Tag> getTags() { return tags; }
    public void setTags(Set<Tag> tags) { this.tags = tags; }
    public Instant getCreated() { return created; }
    public void setCreated(Instant created) { this.created = created; }
    public Instant getModified() { return modified; }
    public void setModified(Instant modified) { this.modified = modified; }
}