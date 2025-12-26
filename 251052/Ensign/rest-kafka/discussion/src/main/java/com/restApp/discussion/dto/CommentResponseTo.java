package com.restApp.discussion.dto;

import com.restApp.discussion.model.CommentState;

public record CommentResponseTo(
                Long id,
                Long newsId,
                String content,
                String country,
                CommentState state) {
}
