package by.bsuir.discussion.dto;

public class MessageResponseTo {

    private Long id;
    private Long storyId;
    private String content;

    public MessageResponseTo() {
    }

    public MessageResponseTo(Long id, Long storyId, String content) {
        this.id = id;
        this.storyId = storyId;
        this.content = content;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
}
