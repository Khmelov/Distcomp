package by.bsuir.distcomp.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class TweetRequestTo {
    private Long id;

    @NotNull(message = "Editor ID must not be null")
    private Long editorId;

    @NotBlank(message = "Title must not be blank")
    @Size(min = 2, max = 64, message = "Title must be between 2 and 64 characters")
    private String title;

    @NotBlank(message = "Content must not be blank")
    @Size(min = 4, max = 2048, message = "Content must be between 4 and 2048 characters")
    private String content;

    public TweetRequestTo() {}

    public TweetRequestTo(Long id, Long editorId, String title, String content) {
        this.id = id;
        this.editorId = editorId;
        this.title = title;
        this.content = content;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getEditorId() { return editorId; }
    public void setEditorId(Long editorId) { this.editorId = editorId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
