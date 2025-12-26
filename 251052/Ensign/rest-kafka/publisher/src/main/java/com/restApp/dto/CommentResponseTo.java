package com.restApp.dto;

import com.restApp.model.CommentState;

public record CommentResponseTo(
                Long id,
                Long newsId,
                String content,
                String country,
                CommentState state) {
}
