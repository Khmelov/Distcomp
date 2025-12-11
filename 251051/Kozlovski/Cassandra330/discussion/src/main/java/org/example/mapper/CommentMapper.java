package org.example.mapper;

import org.example.dto.CommentRequestTo;
import org.example.dto.CommentResponseTo;
import org.example.model.Comment;
import org.example.model.CommentKey;
import org.springframework.stereotype.Component;

@Component
public class CommentMapper {

    public Comment toEntity(CommentRequestTo dto) {
        Comment c = new Comment();
        c.setContent(dto.getContent());
        return c;
    }

    public CommentResponseTo toResponse(Comment entity) {
        CommentKey key = entity.getKey();
        if (key == null) {
            return new CommentResponseTo(null, null, null, entity.getContent());
        }
        return new CommentResponseTo(
                key.getId(),
                key.getTweetId(),
                key.getCountry(),
                entity.getContent()
        );
    }
}