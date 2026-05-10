package com.example.common.dto;

import com.example.common.dto.model.enums.MessageState;

public record MessageResponseTo(
        Long id,
        Long articleId,
        String content,
        MessageState state
) {}
