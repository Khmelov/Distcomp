package by.bsuir.task310.entity;

public class Note {
    private Long id;
    private Long storyId;
    private String content;

    public Note() {
    }

    public Note(Long id, Long storyId, String content) {
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
