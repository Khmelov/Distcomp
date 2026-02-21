package com.example.restApi.services;

import com.example.restApi.dto.request.CommentRequestTo;
import com.example.restApi.dto.response.CommentResponseTo;
import com.example.restApi.exception.NotFoundException;
import com.example.restApi.mapper.CommentMapper;
import com.example.restApi.model.Comment;
import com.example.restApi.repository.CommentRepository;
import com.example.restApi.repository.IssueRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final IssueRepository issueRepository;
    private final CommentMapper commentMapper;

    public CommentService(CommentRepository commentRepository,
                          IssueRepository issueRepository,
                          CommentMapper commentMapper) {
        this.commentRepository = commentRepository;
        this.issueRepository = issueRepository;
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
        if (!issueRepository.existsById(request.getIssueId())) {
            throw new NotFoundException("Issue not found with id: " + request.getIssueId());
        }
        Comment comment = commentMapper.toEntity(request);
        comment.setId(null);
        Comment saved = commentRepository.save(comment);
        return commentMapper.toDto(saved);
    }

    public CommentResponseTo update(CommentRequestTo request) {
        if (request.getId() == null) {
            throw new IllegalArgumentException("Comment ID is required for update");
        }
        Comment comment = commentRepository.findById(request.getId())
                .orElseThrow(() -> new NotFoundException("Comment not found with id: " + request.getId()));
        if (!issueRepository.existsById(request.getIssueId())) {
            throw new NotFoundException("Issue not found with id: " + request.getIssueId());
        }
        commentMapper.updateEntity(comment, request);
        Comment updated = commentRepository.save(comment);
        return commentMapper.toDto(updated);
    }

    public void delete(Long id) {
        if (!commentRepository.existsById(id)) {
            throw new NotFoundException("Comment not found with id: " + id);
        }
        commentRepository.deleteById(id);
    }
}
