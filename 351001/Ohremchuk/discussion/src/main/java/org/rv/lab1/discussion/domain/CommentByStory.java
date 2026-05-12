package org.rv.lab1.discussion.domain;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("tbl_comment")
public class CommentByStory {
    @PrimaryKey
    private CommentByStoryKey key;

    @Column
    private String content;

    @Column
    private String state;

    protected CommentByStory() {
    }

    public CommentByStory(CommentByStoryKey key, String content, CommentState state) {
        this.key = key;
        this.content = content;
        this.state = state == null ? null : state.name();
    }

    public CommentByStoryKey getKey() {
        return key;
    }

    public void setKey(CommentByStoryKey key) {
        this.key = key;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public CommentState getStateEnum() {
        return state == null ? null : CommentState.valueOf(state);
    }

    public void setStateEnum(CommentState state) {
        this.state = state == null ? null : state.name();
    }
}

