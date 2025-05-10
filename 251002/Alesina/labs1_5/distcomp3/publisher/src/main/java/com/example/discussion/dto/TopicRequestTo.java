package com.example.discussion.dto;

import jakarta.validation.constraints.Size;

public class TopicRequestTo {
    private Long id;

    @Size(min = 2, max = 64, message = "Title must be between 2 and 64 characters")
    private String title;

    @Size(min = 4, max = 2048, message = "Content must be between 4 and 2048 characters")
    private String content;

    private Long userId;

    private String[] stickers = new String[0];

    public String[] getStickers(){
        return this.stickers;
    }

    public void setStickers(String[] stickersNames){
        this.stickers = stickersNames;
    }

    // Getters and Setters
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


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}