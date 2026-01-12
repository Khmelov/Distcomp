package com.task.discussion.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NoticeRequestTo {
    private Long id;

    // Убрали @NotBlank - country опционален для PUT запросов
    @Size(min = 2, max = 64, message = "Country must be between 2 and 64 characters")
    private String country;

    @NotNull(message = "TweetId cannot be null")
    private Long tweetId;

    @NotBlank(message = "Content cannot be blank")
    @Size(min = 2, max = 2048, message = "Content must be between 2 and 2048 characters")
    private String content;
}
