package com.lizaveta.discussion.cassandra;

import com.lizaveta.notebook.model.NoticeState;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("tbl_notice_by_story")
public class NoticeByStoryRow {

    @PrimaryKey
    private NoticeByStoryKey key;

    private String content;

    private NoticeState state;

    public NoticeByStoryKey getKey() {
        return key;
    }

    public void setKey(final NoticeByStoryKey key) {
        this.key = key;
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
