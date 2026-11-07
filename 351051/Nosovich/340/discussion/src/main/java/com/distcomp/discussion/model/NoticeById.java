package com.distcomp.discussion.model;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

/**
 * Denormalised copy of {@link Notice} keyed by the notice identifier.
 *
 * <p>Cassandra can only query by the primary key without {@code ALLOW FILTERING}.
 * REST exposes {@code GET /notices/{id}}, therefore the row is duplicated under
 * {@code id}. The copy is updated and removed together with the main row.</p>
 */
@Table("tbl_notice_by_id")
public class NoticeById {

    @PrimaryKey
    private Long id;

    @Column("issue_id")
    private Long issueId;

    private String content;

    private String state;

    public NoticeById() {
    }

    public NoticeById(Long id, Long issueId, String content, String state) {
        this.id = id;
        this.issueId = issueId;
        this.content = content;
        this.state = state;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIssueId() {
        return issueId;
    }

    public void setIssueId(Long issueId) {
        this.issueId = issueId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
