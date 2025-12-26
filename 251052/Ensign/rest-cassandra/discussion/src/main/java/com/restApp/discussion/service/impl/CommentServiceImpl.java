package com.restApp.discussion.service.impl;

import com.restApp.discussion.dto.CommentRequestTo;
import com.restApp.discussion.dto.CommentResponseTo;
import com.restApp.discussion.mapper.CommentMapper;
import com.restApp.discussion.model.Comment;
import com.restApp.discussion.repository.CommentRepository;
import com.restApp.discussion.service.CommentService;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

// For generating random long ID if not using UUID/TIMEUUID.
// Realistically, timeuuid is better for Cassandra, but requirements say 'bigint'.

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

    public CommentServiceImpl(CommentRepository commentRepository, CommentMapper commentMapper) {
        this.commentRepository = commentRepository;
        this.commentMapper = commentMapper;
    }

    @Override
    public CommentResponseTo create(CommentRequestTo request) {
        Comment comment = commentMapper.toEntity(request);
        if (comment.getId() == null) {
            comment.setId(ThreadLocalRandom.current().nextLong(Long.MAX_VALUE)); // Simple random ID
        }
        if (comment.getCountry() == null) {
            comment.setCountry("undefined");
        }
        return commentMapper.toResponse(commentRepository.save(comment));
    }

    @Override
    public CommentResponseTo findById(Long id) {
        return commentRepository.findOneById(id)
                .map(commentMapper::toResponse)
                .orElse(null);
    }

    @Override
    public List<CommentResponseTo> findAll() {
        return commentRepository.findAll().stream()
                .map(commentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CommentResponseTo update(Long id, CommentRequestTo request) {
        return commentRepository.findOneById(id)
                .map(existing -> {
                    existing.setContent(request.content());
                    existing.setNewsId(request.newsId());
                    return commentMapper.toResponse(commentRepository.save(existing));
                })
                .orElse(null);
    }

    @Override
    public void delete(Long id) {
        commentRepository.findOneById(id).ifPresent(commentRepository::delete);
    }

    @Override
    public List<CommentResponseTo> getCommentsByNewsId(Long newsId) {
        return commentRepository.findByNewsId(newsId).stream()
                .map(commentMapper::toResponse)
                .collect(Collectors.toList());
    }
}
