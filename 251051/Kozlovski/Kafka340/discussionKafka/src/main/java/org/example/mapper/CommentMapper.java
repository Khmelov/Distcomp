package org.example.mapper;

import org.example.dto.CommentRequestTo;
import org.example.dto.CommentResponseTo;
import org.example.model.Comment;
import org.example.model.CommentKey;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class CommentMapper {

    public Comment toEntity(CommentRequestTo dto) {
        return new Comment(null, dto.getContent(), "PENDING", LocalDateTime.now());
    }

    public CommentResponseTo toResponse(Comment entity) {
        CommentKey key = entity.getKey();
        LocalDateTime created = entity.getCreated();
        LocalDateTime modified = created;

        if (key == null) {
            return new CommentResponseTo(null, null, null, entity.getContent(), created, modified);
        }
        return new CommentResponseTo(
                key.getId(),
                key.getTweetId(),
                key.getCountry(),
                entity.getContent(),
                created,
                modified
        );
    }
}