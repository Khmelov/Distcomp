package com.distcomp.publisher.comment.dto;

public class CommentResponseTo {

    private long id;
    private long storyId;
    private String content;

    public CommentResponseTo() {
    }

    public CommentResponseTo(long id, long storyId, String content) {
        this.id = id;
        this.storyId = storyId;
        this.content = content;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getStoryId() {
        return storyId;
    }

    public void setStoryId(long storyId) {
        this.storyId = storyId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
