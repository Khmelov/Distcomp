package com.restApp.dto;

public record CommentResponseTo(
        Long id,
        Long newsId,
        String content,
        String country) {
}
