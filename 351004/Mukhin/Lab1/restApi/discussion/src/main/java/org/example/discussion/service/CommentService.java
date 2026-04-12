package org.example.discussion.service;

import org.example.discussion.dto.CommentRequestTo;
import org.example.discussion.dto.CommentResponseTo;
import org.example.discussion.model.Comment;
import org.example.discussion.repository.CommentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentService {

    private final CommentRepository commentRepository;

    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public List<CommentResponseTo> getAll() {
        return commentRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public CommentResponseTo getById(Long id) {
        return commentRepository.findAll().stream()
                .filter(c -> c.getId().equals(id))
                .map(this::toResponse)
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Comment not found with id: " + id));
    }

    public CommentResponseTo create(CommentRequestTo request) {
        Comment comment = new Comment();
        comment.setArticleId(request.getArticleId());
        comment.setContent(request.getContent());
        return toResponse(commentRepository.save(comment));
    }

    public CommentResponseTo update(Long id, CommentRequestTo request) {
        Comment existing = commentRepository.findAll().stream()
                .filter(c -> c.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Comment not found with id: " + id));

        existing.setContent(request.getContent());
        existing.setArticleId(request.getArticleId());
        existing.setModified(LocalDateTime.now());
        return toResponse(commentRepository.save(existing));
    }

    public void delete(Long id) {
        Comment existing = commentRepository.findAll().stream()
                .filter(c -> c.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Comment not found with id: " + id));
        commentRepository.delete(existing);
    }

    private CommentResponseTo toResponse(Comment comment) {
        CommentResponseTo dto = new CommentResponseTo();
        dto.setId(comment.getId());
        dto.setArticleId(comment.getArticleId());
        dto.setContent(comment.getContent());
        dto.setCreated(comment.getCreated());
        dto.setModified(comment.getModified());
        return dto;
    }
}