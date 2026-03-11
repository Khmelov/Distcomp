package com.example.restApi.services;

import com.example.restApi.dto.request.CommentRequestTo;
import com.example.restApi.dto.response.CommentResponseTo;
import com.example.restApi.exception.NotFoundException;
import com.example.restApi.model.Article;
import com.example.restApi.model.Comment;
import com.example.restApi.repository.ArticleRepository;
import com.example.restApi.repository.CommentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final ArticleRepository articleRepository;

    public CommentService(CommentRepository commentRepository,
                          ArticleRepository articleRepository) {
        this.commentRepository = commentRepository;
        this.articleRepository = articleRepository;
    }

    public Page<CommentResponseTo> getAll(int page, int size, String sortParam) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortParam));
        return commentRepository.findAll(pageable)
                .map(this::convertToResponseDto);
    }

    public CommentResponseTo getById(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Comment not found with id: " + id));
        return convertToResponseDto(comment);
    }

    @Transactional
    public CommentResponseTo create(CommentRequestTo request) {
        Article article = articleRepository.findById(request.getArticleId())
                .orElseThrow(() -> new NotFoundException("Article not found with id: " + request.getArticleId()));

        Comment comment = new Comment();
        comment.setText(request.getContent());
        comment.setArticle(article);

        Comment saved = commentRepository.save(comment);
        return convertToResponseDto(saved);
    }

    @Transactional
    public CommentResponseTo update(Long id, CommentRequestTo request) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Comment not found with id: " + id));

        Article article = articleRepository.findById(request.getArticleId())
                .orElseThrow(() -> new NotFoundException("Article not found with id: " + request.getArticleId()));

        comment.setText(request.getContent());
        comment.setArticle(article);
        comment.setModified(LocalDateTime.now());

        Comment updated = commentRepository.save(comment);
        return convertToResponseDto(updated);
    }

    @Transactional
    public void delete(Long id) {
        if (!commentRepository.existsById(id)) {
            throw new NotFoundException("Comment not found with id: " + id);
        }
        commentRepository.deleteById(id);
    }

    public Page<CommentResponseTo> getByArticleId(Long articleId, int page, int size, String sortParam) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortParam));
        return commentRepository.findByArticle_Id(articleId, pageable)
                .map(this::convertToResponseDto);
    }

    private CommentResponseTo convertToResponseDto(Comment comment) {
        CommentResponseTo dto = new CommentResponseTo();
        dto.setId(comment.getId());
        dto.setContent(comment.getText());

        if (comment.getArticle() != null) {
            dto.setArticleId(comment.getArticle().getId());
        }

        dto.setCreated(comment.getCreated());
        dto.setModified(comment.getModified());
        return dto;
    }
}