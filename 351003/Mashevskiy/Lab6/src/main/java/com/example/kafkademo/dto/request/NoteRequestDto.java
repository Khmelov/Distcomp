package com.example.kafkademo.dto.request;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
public class NoteRequestDto {
    @NotBlank
    @Size(min = 1, max = 2048)
    private String content;

    private Long tweetId;
}