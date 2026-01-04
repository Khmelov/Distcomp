package com.example.publisher.service;

import com.example.publisher.client.DiscussionClient;
import com.example.publisher.dto.CommentRequestTo;
import com.example.publisher.dto.CommentResponseTo;
import com.example.publisher.exception.AppException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {
    private final DiscussionClient client;

    public CommentService(DiscussionClient client) {
        this.client = client;
    }

    public List<CommentResponseTo> getAllComments() {
        return client.getAllComments();
    }

    public CommentResponseTo getCommentById(@NotNull Long id) {
        return client.getCommentById(id);
    }

    public List<CommentResponseTo> getCommentsByStoryId(@NotNull Long storyId) {
        return client.getCommentsByStoryId(storyId);
    }

    public CommentResponseTo createComment(@Valid CommentRequestTo request) {
        return client.createComment(request);
    }

    public CommentResponseTo updateComment(@Valid CommentRequestTo request) {
        return client.updateComment(request);
    }

    public void deleteComment(@NotNull Long id) {
        client.deleteComment(id);
    }
}