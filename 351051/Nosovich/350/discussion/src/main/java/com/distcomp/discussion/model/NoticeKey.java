package com.distcomp.discussion.model;

import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

import java.io.Serializable;
import java.util.Objects;

/**
 * Composite key of {@link Notice}.
 *
 * <p>{@code issue_id} is the partition key and {@code id} the clustering column.
 * All notices of one issue therefore live in one partition, and the partition
 * size grows with the discussion of that issue only. The original schema used
 * {@code country} as the partition key, which put every notice of a country into
 * a single, unbounded partition.</p>
 */
@PrimaryKeyClass
public class NoticeKey implements Serializable {

    @PrimaryKeyColumn(name = "issue_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private Long issueId;

    @PrimaryKeyColumn(name = "id", ordinal = 1, type = PrimaryKeyType.CLUSTERED)
    private Long id;

    public NoticeKey() {
    }

    public NoticeKey(Long issueId, Long id) {
        this.issueId = issueId;
        this.id = id;
    }

    public Long getIssueId() {
        return issueId;
    }

    public void setIssueId(Long issueId) {
        this.issueId = issueId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof NoticeKey noticeKey)) {
            return false;
        }
        return Objects.equals(issueId, noticeKey.issueId) && Objects.equals(id, noticeKey.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(issueId, id);
    }
}
