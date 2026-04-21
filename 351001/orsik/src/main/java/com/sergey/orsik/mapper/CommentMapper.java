package com.sergey.orsik.mapper;

import com.sergey.orsik.dto.request.CommentRequestTo;
import com.sergey.orsik.dto.response.CommentResponseTo;
import com.sergey.orsik.entity.Comment;
import org.springframework.stereotype.Component;

@Component
public class CommentMapper {

    public Comment toEntity(CommentRequestTo request) {
        if (request == null) {
            return null;
        }
        return new Comment(
                request.getId(),
                request.getTweetId(),
                request.getContent(),
                request.getCreated()
        );
    }

    public CommentResponseTo toResponse(Comment entity) {
        if (entity == null) {
            return null;
        }
        return new CommentResponseTo(
                entity.getId(),
                entity.getTweetId(),
                entity.getContent(),
                entity.getCreated()
        );
    }
}
