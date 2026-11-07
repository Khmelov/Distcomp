package com.distcomp.discussion.model;

import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

/**
 * Notice stored in {@code distcomp.tbl_notice}.
 *
 * <p>The table is queried by issue, which is the only access path that needs
 * range scans. Point lookups use {@link NoticeById}.</p>
 */
@Table("tbl_notice")
public class Notice {

    @PrimaryKey
    private NoticeKey key;

    private String content;

    private String state;

    public Notice() {
    }

    public Notice(NoticeKey key, String content, String state) {
        this.key = key;
        this.content = content;
        this.state = state;
    }

    public NoticeKey getKey() {
        return key;
    }

    public void setKey(NoticeKey key) {
        this.key = key;
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
