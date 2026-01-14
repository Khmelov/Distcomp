package org.example;

public class NoticeResponseTo {

    private Long id;
    private String content;
    private Long storyId;

    public Long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public Long getStoryId() {
        return storyId;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setStoryId(Long storyId) {
        this.storyId = storyId;
    }
}