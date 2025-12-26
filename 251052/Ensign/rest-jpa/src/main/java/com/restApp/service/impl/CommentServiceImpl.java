package com.restApp.service.impl;

import com.restApp.dto.CommentRequestTo;
import com.restApp.dto.CommentResponseTo;
import com.restApp.exception.BusinessException;
import com.restApp.mapper.CommentMapper;
import com.restApp.model.Author;
import com.restApp.model.Comment;
import com.restApp.model.News;
import com.restApp.repository.AuthorRepository;
import com.restApp.repository.CommentRepository;
import com.restApp.repository.NewsRepository;
import com.restApp.service.CommentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@Transactional
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
        comment.setNews(news);

        Comment savedComment = commentRepository.save(comment);
        news.getComments().add(savedComment);

        return commentMapper.toResponse(savedComment);
    }

    @Override
    public CommentResponseTo update(Long id, CommentRequestTo request) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Comment not found", "40404"));

        if (request.getNewsId() != null && !request.getNewsId().equals(comment.getNews().getId())) {
            News news = newsRepository.findById(request.getNewsId())
                    .orElseThrow(() -> new BusinessException("News not found", "40403"));
            comment.setNews(news);
        }

        commentMapper.updateEntity(comment, request);
        return commentMapper.toResponse(commentRepository.save(comment));
    }

    @Override
    public void delete(Long id) {
        if (!commentRepository.existsById(id)) {
            throw new BusinessException("Comment not found", "40404");
        }
        commentRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public CommentResponseTo findById(Long id) {
        return commentRepository.findById(id)
                .map(commentMapper::toResponse)
                .orElseThrow(() -> new BusinessException("Comment not found", "40404"));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CommentResponseTo> findAll(Pageable pageable) {
        return commentRepository.findAll(pageable)
                .map(commentMapper::toResponse);
    }
}
