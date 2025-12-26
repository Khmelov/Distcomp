package com.restApp.discussion.dto;

public record CommentResponseTo(
        Long id,
        Long newsId,
        String content,
        String country) {
}
