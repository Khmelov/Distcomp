package com.example.discussion.service;

import com.example.discussion.dto.CommentRequestTo;
import com.example.discussion.dto.CommentResponseTo;
import com.example.discussion.exception.AppException;
import com.example.discussion.model.Comment;
import com.example.discussion.repository.CommentRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Service
@Transactional
public class CommentService {
    private final CommentRepository repository;
    private final AtomicLong idGenerator = new AtomicLong(1);

    public CommentService(CommentRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<CommentResponseTo> getAllComments() {
        return repository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public CommentResponseTo getCommentById(@NotNull Long id) {
        return repository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new AppException("Comment not found", 40404));
    }

    @Transactional(readOnly = true)
    public List<CommentResponseTo> getCommentsByStoryId(@NotNull Long storyId) {
        return repository.findByStoryId(storyId).stream()
                .map(this::toResponse)
                .toList();
    }

    public CommentResponseTo createComment(@Valid CommentRequestTo request) {
        Long nextId = idGenerator.getAndIncrement();
        Comment comment = new Comment(request.storyId(), nextId, request.content());
        comment.setCreated(Instant.now());
        Comment saved = repository.save(comment);
        return toResponse(saved);
    }

    public CommentResponseTo updateComment(@Valid CommentRequestTo request) {
        if (request.id() == null) {
            throw new AppException("ID required for update", 40003);
        }
        Comment existing = repository.findById(request.id())
                .orElseThrow(() -> new AppException("Comment not found for update", 40404));
        existing.setContent(request.content());
        Comment updated = repository.save(existing);
        return toResponse(updated);
    }

    public void deleteComment(@NotNull Long id) {
        if (!repository.existsById(id)) {
            throw new AppException("Comment not found for deletion", 40404);
        }
        repository.deleteById(id);
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