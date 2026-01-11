package com.distcomp.publisher.sticker.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class StickerRequest {

    @NotBlank
    @Size(max = 32)
    private String name;

    public StickerRequest() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
