package com.example.restApi.mapper;

import com.example.restApi.dto.request.CommentRequestTo;
import com.example.restApi.dto.response.CommentResponseTo;
import com.example.restApi.model.Comment;
import org.springframework.stereotype.Component;

@Component
public class CommentMapper {

    public Comment toEntity(CommentRequestTo request) {
        Comment comment = new Comment();
        comment.setArticleId(request.getArticleId());
        comment.setContent(request.getContent());
        return comment;
    }

    public CommentResponseTo toDto(Comment comment) {
        CommentResponseTo response = new CommentResponseTo();
        response.setId(comment.getId());
        response.setArticleId(comment.getArticleId());
        response.setContent(comment.getContent());
        return response;
    }

    public void updateEntity(Comment comment, CommentRequestTo request) {
        comment.setArticleId(request.getArticleId());
        comment.setContent(request.getContent());
    }
}
