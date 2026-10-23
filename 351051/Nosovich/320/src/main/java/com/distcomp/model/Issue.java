package com.distcomp.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Problem statement, stored in {@code distcomp.tbl_issue}.
 *
 * <p>Owns the {@code Issue-Tag} many-to-many relation through the
 * {@code distcomp.tbl_issue_tag} join table. Timestamps are maintained by the
 * service layer.</p>
 */
@Entity
@Table(name = "tbl_issue", schema = "distcomp",
        uniqueConstraints = @UniqueConstraint(name = "uk_issue_title", columnNames = "title"))
public class Issue extends AbstractEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "creator_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_issue_creator"))
    private Creator creator;

    @Column(name = "title", nullable = false, length = 64)
    private String title;

    @Column(name = "content", nullable = false, length = 2048)
    private String content;

    @Column(name = "created", nullable = false)
    private Instant created;

    @Column(name = "modified", nullable = false)
    private Instant modified;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "tbl_issue_tag", schema = "distcomp",
            joinColumns = @JoinColumn(name = "issue_id",
                    foreignKey = @ForeignKey(name = "fk_issue_tag_issue")),
            inverseJoinColumns = @JoinColumn(name = "tag_id",
                    foreignKey = @ForeignKey(name = "fk_issue_tag_tag")),
            uniqueConstraints = @UniqueConstraint(name = "uk_issue_tag",
                    columnNames = {"issue_id", "tag_id"}))
    private Set<Tag> tags = new LinkedHashSet<>();

    public Creator getCreator() {
        return creator;
    }

    public void setCreator(Creator creator) {
        this.creator = creator;
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

    public Instant getCreated() {
        return created;
    }

    public void setCreated(Instant created) {
        this.created = created;
    }

    public Instant getModified() {
        return modified;
    }

    public void setModified(Instant modified) {
        this.modified = modified;
    }

    public Set<Tag> getTags() {
        return tags;
    }

    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }

    @Override
    public String toString() {
        return "Issue{id=" + getId() + ", title='" + title + "'}";
    }
}
