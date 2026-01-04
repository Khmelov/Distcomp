package com.example.storyapp.service;

import com.example.storyapp.dto.CommentRequestTo;
import com.example.storyapp.dto.CommentResponseTo;
import com.example.storyapp.exception.AppException;
import com.example.storyapp.model.Comment;
import com.example.storyapp.model.Story;
import com.example.storyapp.repository.CommentRepository;
import com.example.storyapp.repository.StoryRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@Transactional
public class CommentService {
    private final CommentRepository commentRepo;
    private final StoryRepository storyRepo;

    public CommentService(CommentRepository commentRepo, StoryRepository storyRepo) {
        this.commentRepo = commentRepo;
        this.storyRepo = storyRepo;
    }

    @Transactional(readOnly = true)
    public List<CommentResponseTo> getAllComments() {
        return commentRepo.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public CommentResponseTo getCommentById(@NotNull Long id) {
        return commentRepo.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new AppException("Comment not found", 40404));
    }

    public CommentResponseTo createComment(@Valid CommentRequestTo request) {
        Story story = storyRepo.findById(request.storyId())
                .orElseThrow(() -> new AppException("Story not found", 40402));

        Comment comment = new Comment(story, request.content());
        comment.setCreated(Instant.now());
        Comment saved = commentRepo.save(comment);
        return toResponse(saved);
    }

    public CommentResponseTo updateComment(@Valid CommentRequestTo request) {
        if (request.id() == null) {
            throw new AppException("ID required for update", 40003);
        }

        Comment existing = commentRepo.findById(request.id())
                .orElseThrow(() -> new AppException("Comment not found for update", 40404));

        Story story = storyRepo.findById(request.storyId())
                .orElseThrow(() -> new AppException("Story not found", 40402));

        existing.setStory(story);
        existing.setContent(request.content());

        Comment updated = commentRepo.save(existing);
        return toResponse(updated);
    }

    public void deleteComment(@NotNull Long id) {
        if (!commentRepo.existsById(id)) {
            throw new AppException("Comment not found for deletion", 40404);
        }
        commentRepo.deleteById(id);
    }

    private CommentResponseTo toResponse(Comment comment) {
        return new CommentResponseTo(
                comment.getId(),
                comment.getStory().getId(),
                comment.getContent(),
                comment.getCreated()
        );
    }
}