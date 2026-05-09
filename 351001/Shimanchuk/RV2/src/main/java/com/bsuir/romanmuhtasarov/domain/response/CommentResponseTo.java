package com.bsuir.romanmuhtasarov.domain.response;

public record CommentResponseTo(
        Long id,
        Long newsId,
        String content) {
}
