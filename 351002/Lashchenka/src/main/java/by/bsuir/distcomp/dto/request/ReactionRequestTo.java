package by.bsuir.distcomp.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ReactionRequestTo {
    private Long id;

    @NotNull(message = "Tweet ID must not be null")
    private Long tweetId;

    @NotBlank(message = "Content must not be blank")
    @Size(min = 2, max = 2048, message = "Content must be between 2 and 2048 characters")
    private String content;

    public ReactionRequestTo() {}

    public ReactionRequestTo(Long id, Long tweetId, String content) {
        this.id = id;
        this.tweetId = tweetId;
        this.content = content;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getTweetId() { return tweetId; }
    public void setTweetId(Long tweetId) { this.tweetId = tweetId; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
