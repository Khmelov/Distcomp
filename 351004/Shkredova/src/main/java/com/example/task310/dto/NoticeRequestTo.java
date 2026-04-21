package com.example.task310.dto;

import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class NoticeRequestTo {
    private Long id;  // ← ЭТО ДОБАВЬ

    @NotNull(message = "News ID is required")
    private Long newsId;

    @NotBlank(message = "Content is required")
    @Size(min = 2, max = 2048, message = "Content must be between 2 and 2048 characters")
    private String content;
}