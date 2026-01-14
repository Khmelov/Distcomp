package org.example;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class NoticeRequestTo {

    @NotBlank
    @Size(min = 2, max = 2048)
    private String content;
    @NotNull
    private Long storyId;


    public String getContent() {
        return content;
    }

    public Long getStoryId() {
        return storyId;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setStoryId(Long storyId) {
        this.storyId = storyId;
    }
}