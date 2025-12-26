package com.restApp.mapper;

import com.restApp.dto.CommentRequestTo;
import com.restApp.dto.CommentResponseTo;
import com.restApp.model.Comment;
import org.springframework.stereotype.Component;

@Component
public class CommentMapper {

    public Comment toEntity(CommentRequestTo request) {
        Comment comment = new Comment();
        comment.setContent(request.getContent());
        comment.setNewsId(request.getNewsId());
        return comment;
    }

    public CommentResponseTo toResponse(Comment entity) {
        CommentResponseTo response = new CommentResponseTo();
        response.setId(entity.getId());
        response.setContent(entity.getContent());
        response.setTimestamp(entity.getTimestamp());
        response.setNewsId(entity.getNewsId());
        response.setAuthorLogin(entity.getAuthorLogin());
        return response;
    }

    public void updateEntity(Comment entity, CommentRequestTo request) {
        if (request.getContent() != null)
            entity.setContent(request.getContent());
    }
}
