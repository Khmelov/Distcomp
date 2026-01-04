package com.example.storyapp.service;

import com.example.storyapp.dto.CommentRequestTo;
import com.example.storyapp.dto.CommentResponseTo;
import com.example.storyapp.exception.AppException;
import com.example.storyapp.model.Comment;
import com.example.storyapp.repository.InMemoryCommentRepository;
import com.example.storyapp.repository.InMemoryStoryRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {
    private final InMemoryCommentRepository commentRepo;
    private final InMemoryStoryRepository storyRepo;

    public CommentService(InMemoryCommentRepository commentRepo, InMemoryStoryRepository storyRepo) {
        this.commentRepo = commentRepo;
        this.storyRepo = storyRepo;
    }

    public List<CommentResponseTo> getAllComments() {
        return commentRepo.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public CommentResponseTo getCommentById(@NotNull Long id) {
        return commentRepo.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new AppException("Comment not found", 40404));
    }

    public CommentResponseTo createComment(@Valid CommentRequestTo request) {
        if (!storyRepo.findById(request.storyId()).isPresent()) {
            throw new AppException("Story not found for comment", 40402);
        }
        Comment comment = toEntity(request);
        comment.setCreated(java.time.Instant.now());
        Comment saved = commentRepo.save(comment);
        return toResponse(saved);
    }

    public CommentResponseTo updateComment(@Valid CommentRequestTo request) {
        if (request.id() == null) {
            throw new AppException("ID required for update", 40003);
        }
        if (!commentRepo.findById(request.id()).isPresent()) {
            throw new AppException("Comment not found for update", 40404);
        }
        if (!storyRepo.findById(request.storyId()).isPresent()) {
            throw new AppException("Story not found for comment update", 40402);
        }
        Comment comment = toEntity(request);
        Comment updated = commentRepo.save(comment);
        return toResponse(updated);
    }

    public void deleteComment(@NotNull Long id) {
        if (!commentRepo.deleteById(id)) {
            throw new AppException("Comment not found for deletion", 40404);
        }
    }

    private Comment toEntity(CommentRequestTo dto) {
        Comment comment = new Comment();
        comment.setId(dto.id());
        comment.setStoryId(dto.storyId());
        comment.setContent(dto.content());
        return comment;
    }

    private CommentResponseTo toResponse(Comment comment) {
        return new CommentResponseTo(
                comment.getId(),
                comment.getStoryId(),
                comment.getContent(),
                comment.getCreated()
        );
    }
}