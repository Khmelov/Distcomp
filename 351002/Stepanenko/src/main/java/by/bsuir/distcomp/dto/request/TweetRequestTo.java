package by.bsuir.distcomp.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Set;

public class TweetRequestTo {
    private Long id;

    @NotNull(message = "Author ID is required")
    @JsonProperty("authorId")
    private Long authorId;

    @NotBlank(message = "Title is required")
    @Size(min = 2, max = 64, message = "Title must be between 2 and 64 characters")
    @JsonProperty("title")
    private String title;

    @NotBlank(message = "Content is required")
    @Size(min = 4, max = 2048, message = "Content must be between 4 and 2048 characters")
    @JsonProperty("content")
    private String content;

    @JsonProperty("markerIds")
    private Set<Long> markerIds;

    public TweetRequestTo() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Set<Long> getMarkerIds() {
        return markerIds;
    }

    public void setMarkerIds(Set<Long> markerIds) {
        this.markerIds = markerIds;
    }
}
