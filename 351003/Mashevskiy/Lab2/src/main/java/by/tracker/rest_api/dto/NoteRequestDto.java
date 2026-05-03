package by.tracker.rest_api.dto;

import jakarta.validation.constraints.*;

public class NoteRequestDto {
    private Long id;

    @NotNull(message = "Tweet ID is required")
    private Long tweetId;

    @NotBlank(message = "Content is required")
    @Size(min = 2, max = 2048, message = "Content must be between 2 and 2048 characters")
    private String content;
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getTweetId() { return tweetId; }
    public void setTweetId(Long tweetId) { this.tweetId = tweetId; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}