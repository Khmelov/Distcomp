package com.example.Task310.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder



public class PostRequestTo {
    private Long id;

    @NotBlank(message = "Content is required")
    @Size(min = 2, max = 2048)
    private String content;

    @NotNull(message = "Story ID is required")
    private Long storyId;
}