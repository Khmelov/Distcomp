package com.example.restApi.services;

import com.example.restApi.dto.request.CommentRequestTo;
import com.example.restApi.dto.response.CommentResponseTo;
import com.example.restApi.exception.NotFoundException;
import com.example.restApi.mapper.CommentMapper;
import com.example.restApi.model.Comment;
import com.example.restApi.repository.CommentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

    public CommentService(CommentRepository commentRepository, CommentMapper commentMapper) {
        this.commentRepository = commentRepository;
        this.commentMapper = commentMapper;
    }

    public List<CommentResponseTo> getAll() {
        return commentRepository.findAll().stream()
                .map(commentMapper::toDto)
                .collect(Collectors.toList());
    }

    public CommentResponseTo getById(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Comment not found with id: " + id));
        return commentMapper.toDto(comment);
    }

    public CommentResponseTo create(CommentRequestTo request) {
        Comment comment = commentMapper.toEntity(request);
        Comment saved = commentRepository.save(comment);
        return commentMapper.toDto(saved);
    }

    public CommentResponseTo update(Long id, CommentRequestTo request) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Comment not found with id: " + id));
        commentMapper.updateEntity(comment, request);
        comment.setModified(LocalDateTime.now());
        Comment updated = commentRepository.save(comment);
        return commentMapper.toDto(updated);
    }

    public void delete(Long id) {
        if (!commentRepository.existsById(id)) {
            throw new NotFoundException("Comment not found with id: " + id);
        }
        commentRepository.deleteById(id);
    }

    public List<CommentResponseTo> getByArticleId(Long articleId) {
        return commentRepository.findByArticleId(articleId).stream()
                .map(commentMapper::toDto)
                .collect(Collectors.toList());
    }
}
