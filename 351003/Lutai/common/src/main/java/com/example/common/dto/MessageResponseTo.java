package com.example.common.dto;

public record MessageResponseTo(
        Long id,
        Long articleId,
        String content
) {}
