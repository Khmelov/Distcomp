package by.ryabchikov.comment_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record CommentRequestTo(
        @NotNull(message = "Tweet-ID should not be null.")
        @Positive(message = "Tweet-ID should be a positive number.")
        Long tweetId,

        @NotBlank(message = "Content should not be blank.")
        @Size(min = 2, max = 2048, message = "Content size should be between 2 and 2048 chars.")
        String content
) {
}
