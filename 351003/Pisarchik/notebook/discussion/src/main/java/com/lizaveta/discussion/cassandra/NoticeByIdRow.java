package com.lizaveta.discussion.cassandra;

import com.lizaveta.notebook.model.NoticeState;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("tbl_notice_by_id")
public class NoticeByIdRow {

    @PrimaryKey
    private Long id;

    @Column("story_id")
    private Long storyId;

    private String content;

    private NoticeState state;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public Long getStoryId() {
        return storyId;
    }

    public void setStoryId(final Long storyId) {
        this.storyId = storyId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(final String content) {
        this.content = content;
    }

    public NoticeState getState() {
        return state;
    }

    public void setState(final NoticeState state) {
        this.state = state;
    }
}
