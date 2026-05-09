package com.example.demo.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "tbl_story", schema = "distcomp")
public class Story extends BaseEntity{
    @ManyToOne(optional = false)
    @JoinColumn(name = "writer_id", nullable = false)
    private Writer writer;

    @Column(nullable = false, length = 64)
    private String title;

    @Column(nullable = false, length = 2048)
    private String content;

    @Column(name = "created")
    private LocalDateTime created;

    @Column(name = "modified")
    private LocalDateTime modified;

    @ManyToMany
    @JoinTable(
            name = "tbl_story_tag",
            schema = "distcomp",
            joinColumns = @JoinColumn(name = "story_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new HashSet<>();

    public void setWriter(Writer writer) {
        this.writer = writer;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public void setModified(LocalDateTime modified) {
        this.modified = modified;
    }
    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }
    // Геттер для created
    public LocalDateTime getCreated() {
        return created;
    }

    // Геттер для tags
    public Set<Tag> getTags() {
        return tags;
    }
}
