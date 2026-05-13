package org.example.newsapi.service;

import lombok.RequiredArgsConstructor;
import org.example.newsapi.dto.request.CommentRequestTo;
import org.example.newsapi.dto.response.CommentResponseTo;
import org.example.newsapi.entity.Comment;
import org.example.newsapi.entity.News;
import org.example.newsapi.exception.NotFoundException;
import org.example.newsapi.mapper.CommentMapper;
import org.example.newsapi.repository.CommentRepository;
import org.example.newsapi.repository.NewsRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;
    private final NewsRepository newsRepository;
    private final CommentMapper commentMapper;

    @Transactional
    public CommentResponseTo create(CommentRequestTo request) {
        // Находим новость, к которой пишется комментарий
        News news = newsRepository.findById(request.getNewsId())
                .orElseThrow(() -> new NotFoundException("News not found with id: " + request.getNewsId()));

        Comment comment = commentMapper.toEntity(request);
        comment.setNews(news);

        return commentMapper.toDto(commentRepository.save(comment));
    }

    public Page<CommentResponseTo> findAll(Pageable pageable) {
        return commentRepository.findAll(pageable)
                .map(commentMapper::toDto);
    }

    public CommentResponseTo findById(Long id) {
        return commentRepository.findById(id)
                .map(commentMapper::toDto)
                .orElseThrow(() -> new NotFoundException("Comment not found with id: " + id));
    }

    @Transactional
    public CommentResponseTo update(Long id, CommentRequestTo request) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Comment not found with id: " + id));

        commentMapper.updateEntityFromDto(request, comment);

        // Если меняется привязка к новости (редкий кейс, но возможный)
        if (request.getNewsId() != null && !request.getNewsId().equals(comment.getNews().getId())) {
            News news = newsRepository.findById(request.getNewsId())
                    .orElseThrow(() -> new NotFoundException("News not found"));
            comment.setNews(news);
        }

        return commentMapper.toDto(commentRepository.save(comment));
    }

    @Transactional
    public void delete(Long id) {
        if (!commentRepository.existsById(id)) {
            throw new NotFoundException("Comment not found with id: " + id);
        }
        commentRepository.deleteById(id);
    }
}