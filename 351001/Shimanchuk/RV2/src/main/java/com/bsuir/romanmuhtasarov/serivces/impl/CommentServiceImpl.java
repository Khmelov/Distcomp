package com.bsuir.romanmuhtasarov.serivces.impl;

import com.bsuir.romanmuhtasarov.domain.entity.Comment;
import com.bsuir.romanmuhtasarov.domain.entity.News;
import com.bsuir.romanmuhtasarov.domain.entity.ValidationMarker;
import com.bsuir.romanmuhtasarov.domain.mapper.CommentListMapper;
import com.bsuir.romanmuhtasarov.domain.mapper.CommentMapper;
import com.bsuir.romanmuhtasarov.domain.request.CommentRequestTo;
import com.bsuir.romanmuhtasarov.domain.response.CommentResponseTo;
import com.bsuir.romanmuhtasarov.exceptions.NoSuchCommentException;
import com.bsuir.romanmuhtasarov.exceptions.NoSuchNewsException;
import com.bsuir.romanmuhtasarov.repositories.CommentRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import com.bsuir.romanmuhtasarov.serivces.CommentService;
import com.bsuir.romanmuhtasarov.serivces.NewsService;

import java.util.List;

@Service
@Transactional
@Validated
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final NewsService newsService;
    private final CommentMapper commentMapper;
    private final CommentListMapper commentListMapper;

    @Autowired
    public CommentServiceImpl(CommentRepository commentRepository, NewsService newsService, CommentMapper commentMapper, CommentListMapper commentListMapper) {
        this.commentRepository = commentRepository;
        this.newsService = newsService;
        this.commentMapper = commentMapper;
        this.commentListMapper = commentListMapper;
    }

    @Override
    @Validated(ValidationMarker.OnCreate.class)
    public CommentResponseTo create(@Valid CommentRequestTo entity) {
        News news = newsService.findNewsByIdExt(entity.newsId()).orElseThrow(() -> new NoSuchNewsException(entity.newsId()));
        Comment comment = commentMapper.toComment(entity);
        comment.setNews(news);
        return commentMapper.toCommentResponseTo(commentRepository.save(comment));
    }

    @Override
    public List<CommentResponseTo> read() {
        return commentListMapper.toCommentResponseToList(commentRepository.findAll());
    }

    @Override
    @Validated(ValidationMarker.OnUpdate.class)
    public CommentResponseTo update(@Valid CommentRequestTo entity) {
        if (commentRepository.existsById(entity.id())) {
            Comment comment = commentMapper.toComment(entity);
            News tweetRef = newsService.findNewsByIdExt(comment.getNews().getId()).orElseThrow(() -> new NoSuchNewsException(comment.getNews().getId()));
            comment.setNews(tweetRef);
            return commentMapper.toCommentResponseTo(commentRepository.save(comment));
        } else {
            throw new NoSuchCommentException(entity.id());
        }
    }

    @Override
    public void delete(Long id) {
        if (commentRepository.existsById(id)) {
            commentRepository.deleteById(id);
        } else {
            throw new NoSuchCommentException(id);
        }
    }

    @Override
    public CommentResponseTo findCommentById(Long id) {
        Comment comment = commentRepository.findById(id).orElseThrow(() -> new NoSuchCommentException(id));
        return commentMapper.toCommentResponseTo(comment);
    }
}
