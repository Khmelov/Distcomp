package org.example.discussion.service;

import org.example.discussion.dto.CommentRequestTo;
import org.example.discussion.dto.CommentResponseTo;
import org.example.discussion.model.Comment;
import org.example.discussion.model.CommentState;
import org.example.discussion.repository.CommentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Arrays;
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
        comment.setState(CommentState.PENDING.name());
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

    private static final List<String> STOP_WORDS = Arrays.asList(
            "spam", "fuck", "shit", "damn", "hate", "kill", "violence", "drugs"
    );

    public CommentResponseTo createFromKafka(Long id, Long articleId, String content) {
        Comment comment = new Comment();
        comment.setId(id);
        comment.setArticleId(articleId);
        comment.setContent(content);
        comment.setState(moderate(content));
        return toResponse(commentRepository.save(comment));
    }

    public CommentResponseTo updateFromKafka(Long id, Long articleId, String content) {
        Comment existing = commentRepository.findAll().stream()
                .filter(c -> c.getId().equals(id)).findFirst().orElse(null);
        if (existing == null) {
            return createFromKafka(id, articleId, content);
        }
        existing.setContent(content);
        existing.setArticleId(articleId);
        existing.setState(moderate(content));
        existing.setModified(LocalDateTime.now());
        return toResponse(commentRepository.save(existing));
    }

    private CommentResponseTo toResponse(Comment comment) {
        CommentResponseTo dto = new CommentResponseTo();
        dto.setId(comment.getId());
        dto.setArticleId(comment.getArticleId());
        dto.setContent(comment.getContent());
        dto.setState(comment.getState());
        dto.setCreated(comment.getCreated());
        dto.setModified(comment.getModified());
        return dto;
    }

    public String moderate(String content) {
        if (content == null) return CommentState.DECLINE.name();
        String lower = content.toLowerCase();
        for (String word : STOP_WORDS) {
            if (lower.contains(word)) {
                return CommentState.DECLINE.name();
            }
        }
        return CommentState.APPROVE.name();
    }


}