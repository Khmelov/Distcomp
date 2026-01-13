package com.publick.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "tbl_issue_sticker", schema = "distcomp",
       uniqueConstraints = @UniqueConstraint(columnNames = {"issue_id", "sticker_id"}))
public class IssueSticker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "issue_id", nullable = false)
    private Issue issue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sticker_id", nullable = false)
    private Sticker sticker;

    public IssueSticker() {
    }

    public IssueSticker(Issue issue, Sticker sticker) {
        this.issue = issue;
        this.sticker = sticker;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Issue getIssue() {
        return issue;
    }

    public void setIssue(Issue issue) {
        this.issue = issue;
    }

    public Sticker getSticker() {
        return sticker;
    }

    public void setSticker(Sticker sticker) {
        this.sticker = sticker;
    }
}