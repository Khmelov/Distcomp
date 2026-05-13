package com.example.publisher.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ReactionRequestTo {
    @NotNull
    private Long tweetId;

    @Size(min = 2, max = 512)
    private String content;
}