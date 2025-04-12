package by.ryabchikov.comment_service.client;

import lombok.Builder;

import java.time.LocalDateTime;


@Builder
public record TweetResponseTo(
        Long id,
        Long creatorId,
        String title,
        String content,
        LocalDateTime createdTime,
        LocalDateTime lastModifiedTime
) {
}
