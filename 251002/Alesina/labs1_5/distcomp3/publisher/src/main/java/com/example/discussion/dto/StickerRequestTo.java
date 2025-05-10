package com.example.discussion.dto;

import jakarta.validation.constraints.Size;

public class StickerRequestTo {
    private Long id;

    @Size(min = 2, max = 32, message = "Sticker name must be between 2 and 32 characters")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}