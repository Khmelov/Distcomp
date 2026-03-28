package org.example.newsapi.service;


import lombok.RequiredArgsConstructor;
import org.example.newsapi.dto.request.CommentRequestTo;
import org.example.newsapi.dto.response.CommentResponseTo;
import org.example.newsapi.entity.Comment;
import org.example.newsapi.exception.NotFoundException;
import org.example.newsapi.mapper.CommentMapper;
import org.example.newsapi.repository.impl.CommentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

    public CommentResponseTo create(CommentRequestTo request) {
        Comment comment = commentMapper.toEntity(request);
        return commentMapper.toDto(commentRepository.save(comment));
    }

    public List<CommentResponseTo> findAll() {
        return commentRepository.findAll().stream()
                .map(commentMapper::toDto)
                .collect(Collectors.toList());
    }

    public CommentResponseTo findById(Long id) {
        return commentRepository.findById(id)
                .map(commentMapper::toDto)
                .orElseThrow(() -> new NotFoundException("Comment not found with id: " + id));
    }

    public CommentResponseTo update(Long id, CommentRequestTo request) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Comment not found with id: " + id));
        commentMapper.updateEntityFromDto(request, comment);
        return commentMapper.toDto(commentRepository.update(comment));
    }

    public void delete(Long id) {
        if (commentRepository.findById(id).isEmpty()) {
            throw new NotFoundException("Comment not found with id: " + id);
        }
        commentRepository.deleteById(id);
    }
    public List<CommentResponseTo> findByNewsId(Long newsId) {
        return commentRepository.findAll().stream()
                .filter(c -> c.getNewsId().equals(newsId))
                .map(commentMapper::toDto)
                .collect(Collectors.toList());
    }

}