package com.distcomp.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/**
 * Remark attached to an issue, stored in {@code distcomp.tbl_notice}.
 */
@Entity
@Table(name = "tbl_notice", schema = "distcomp")
public class Notice extends AbstractEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "issue_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_notice_issue"))
    private Issue issue;

    @Column(name = "content", nullable = false, length = 2048)
    private String content;

    public Issue getIssue() {
        return issue;
    }

    public void setIssue(Issue issue) {
        this.issue = issue;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "Notice{id=" + getId() + ", content='" + content + "'}";
    }
}
