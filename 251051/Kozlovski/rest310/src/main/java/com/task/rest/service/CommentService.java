package com.task.rest.service;

import com.task.rest.dto.CommentRequestTo;
import com.task.rest.dto.CommentResponseTo;
import com.task.rest.model.Comment;
import com.task.rest.repository.InMemoryRepository;
import com.task.rest.util.CommentMapper;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentService {
    private final InMemoryRepository<Comment> commentRepository;
    private final CommentMapper commentMapper;

    public CommentService(InMemoryRepository<Comment> commentRepository, CommentMapper commentMapper) {
        this.commentRepository = commentRepository;
        this.commentMapper = commentMapper;
    }

    public CommentResponseTo createComment(@Valid CommentRequestTo requestTo) {
        Comment comment = commentMapper.toEntity(requestTo);
        Comment savedComment = commentRepository.save(comment);
        return commentMapper.toResponse(savedComment);
    }

    public CommentResponseTo getCommentById(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found with id: " + id));
        return commentMapper.toResponse(comment);
    }

    public List<CommentResponseTo> getAllComments() {
        return commentRepository.findAll().stream()
                .map(commentMapper::toResponse)
                .collect(Collectors.toList());
    }

    public CommentResponseTo updateComment(Long id, @Valid CommentRequestTo requestTo) {
        Comment existingComment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found with id: " + id));
        commentMapper.updateEntityFromDto(requestTo, existingComment);
        Comment updatedComment = commentRepository.save(existingComment);
        return commentMapper.toResponse(updatedComment);
    }

    public void deleteComment(Long id) {
        if (!commentRepository.existsById(id)) {
            throw new RuntimeException("Comment not found with id: " + id);
        }
        commentRepository.deleteById(id);
    }

    public List<CommentResponseTo> getCommentsByTweetId(Long tweetId) {
        return commentRepository.findByTweetId(tweetId).stream()
                .map(commentMapper::toResponse)
                .collect(Collectors.toList());
    }
}