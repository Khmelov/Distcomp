package com.restApp.service.impl;

import com.restApp.dto.CommentRequestTo;
import com.restApp.dto.CommentResponseTo;
import com.restApp.exception.BusinessException;
import com.restApp.mapper.CommentMapper;
import com.restApp.model.Comment;
import com.restApp.model.News;
import com.restApp.repository.CommentRepository;
import com.restApp.repository.NewsRepository;
import com.restApp.service.CommentService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final NewsRepository newsRepository;
    private final CommentMapper commentMapper;

    public CommentServiceImpl(CommentRepository commentRepository, NewsRepository newsRepository,
            CommentMapper commentMapper) {
        this.commentRepository = commentRepository;
        this.newsRepository = newsRepository;
        this.commentMapper = commentMapper;
    }

    @Override
    public CommentResponseTo create(CommentRequestTo request) {
        News news = newsRepository.findById(request.getNewsId())
                .orElseThrow(() -> new BusinessException("News not found", "40403"));

        Comment comment = commentMapper.toEntity(request);
        comment.setTimestamp(Instant.now());

        Comment savedComment = commentRepository.save(comment);
        news.getComments().add(savedComment);

        return commentMapper.toResponse(savedComment);
    }

    @Override
    public CommentResponseTo update(Long id, CommentRequestTo request) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Comment not found", "40404"));

        commentMapper.updateEntity(comment, request);
        return commentMapper.toResponse(commentRepository.save(comment));
    }

    @Override
    public void delete(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Comment not found", "40404"));

        newsRepository.findById(comment.getNewsId())
                .ifPresent(news -> news.getComments().remove(comment));

        commentRepository.deleteById(id);
    }

    @Override
    public CommentResponseTo findById(Long id) {
        return commentRepository.findById(id)
                .map(commentMapper::toResponse)
                .orElseThrow(() -> new BusinessException("Comment not found", "40404"));
    }

    @Override
    public List<CommentResponseTo> findAll() {
        return commentRepository.findAll().stream()
                .map(commentMapper::toResponse)
                .collect(Collectors.toList());
    }
}
