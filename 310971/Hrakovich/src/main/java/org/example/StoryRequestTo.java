package org.example;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public class StoryRequestTo {

    @NotBlank
    @Size(min = 3, max = 64)
    @JsonDeserialize(using = StrictStringDeserializer.class)
    private String title;
    @NotBlank
    @Size(min = 3, max = 2048)
    @JsonDeserialize(using = StrictStringDeserializer.class)
    private String content;
    @NotNull
    private Long writerId;
    private List<String> storyTags;

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public Long getWriterId() {
        return writerId;
    }

    public List<String> getStoryTags() {
        return storyTags;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setWriterId(Long writerId) {
        this.writerId = writerId;
    }

    public void setStoryTags(List<String> storyTags) {
        this.storyTags = storyTags;
    }
}