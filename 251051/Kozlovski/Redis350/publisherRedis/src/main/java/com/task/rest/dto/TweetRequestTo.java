package com.task.rest.dto;

import com.task.rest.model.Mark;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

public class TweetRequestTo {
    @NotNull(message = "Writer ID cannot be null")
    private Long writerId;

    @NotBlank(message = "Title cannot be blank")
    @Size(min = 4, max = 64, message = "Title must be at most 64 characters")
    private String title;

    @NotBlank(message = "Content cannot be blank")
    @Size(max = 2048, message = "Content must be at most 2048 characters")
    private String content;

    private List<String> marks = new ArrayList<>();

    public TweetRequestTo() {}

    public Long getWriterId() { return writerId; }
    public void setWriterId(Long writerId) { this.writerId = writerId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public List<String> getMarks() {
        return marks;
    }
    public void setMarks(List<String> marks) {
        this.marks = marks;
    }
}
