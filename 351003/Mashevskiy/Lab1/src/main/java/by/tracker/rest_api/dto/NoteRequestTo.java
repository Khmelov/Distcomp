package by.tracker.rest_api.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Data
public class NoteRequestTo {
    private Long id;

    @NotNull(message = "tweetId is required")
    private Long tweetId;  // ← должно быть Long

    @NotBlank(message = "Content is required")
    @Size(min = 2, max = 2048, message = "Content must be between 2 and 2048 characters")
    private String content;
}