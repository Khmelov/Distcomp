package org.rv.lab1.discussion.domain;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("tbl_comment_by_id")
public class CommentById {
    @PrimaryKey
    private Long id;

    @Column("story_id")
    private Long storyId;

    @Column
    private String content;

    @Column
    private String state;

    protected CommentById() {
    }

    public CommentById(Long id, Long storyId, String content, CommentState state) {
        this.id = id;
        this.storyId = storyId;
        this.content = content;
        this.state = state == null ? null : state.name();
    }

    public Long getId() {
        return id;
    }

    public Long getStoryId() {
        return storyId;
    }

    public void setStoryId(Long storyId) {
        this.storyId = storyId;
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

