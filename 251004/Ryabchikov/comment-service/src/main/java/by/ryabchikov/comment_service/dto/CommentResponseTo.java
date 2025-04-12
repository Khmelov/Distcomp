package by.ryabchikov.comment_service.dto;

import lombok.Builder;

@Builder
public record CommentResponseTo(
        Long id,
        Long tweetId,
        String content
) {
}
