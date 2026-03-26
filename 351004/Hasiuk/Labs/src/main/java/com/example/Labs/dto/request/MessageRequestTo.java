package com.example.Labs.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class MessageRequestTo {
    @NotNull
    private Long storyId;
    @NotBlank
    @Size(min = 2, max = 2048)
    private String content;
}