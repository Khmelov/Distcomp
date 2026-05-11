package by.tracker.rest_api.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Data
public class TweetRequestTo {
    private Long id;

    @NotNull(message = "creatorId is required")
    private Long creatorId;  // ← должно быть Long, не String

    @NotBlank(message = "Title is required")
    @Size(min = 2, max = 64, message = "Title must be between 2 and 64 characters")
    private String title;

    @NotBlank(message = "Content is required")
    @Size(min = 4, max = 2048, message = "Content must be between 4 and 2048 characters")
    private String content;
}