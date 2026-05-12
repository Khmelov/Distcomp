package org.discussion.service.comment;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.discussion.dto.comment.CommentRequestTo;
import org.discussion.dto.comment.CommentResponseTo;
import org.discussion.entity.comment.Comment;
import org.discussion.entity.comment.CommentKey;
import org.discussion.exception.NotFoundException;
import org.discussion.repository.comment.CommentRepository;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.client.RestClient;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Service
@Validated
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;

    public List<CommentResponseTo> getAllComments() {
        return commentRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    public CommentResponseTo getComment(Long id) {
        return commentRepository.findByKeyId(id)
                .map(this::mapToResponse)
                .orElseThrow(() -> new NotFoundException("Comment not found"));
    }

    public CommentResponseTo createComment(@Valid CommentRequestTo dto) {
        Comment comment = new Comment();

        CommentKey key = new CommentKey();
        key.setCountry(dto.getCountry());
        key.setIssueId(dto.getIssueId());
        key.setId(dto.getId() != null ? dto.getId() : System.currentTimeMillis());

        comment.setKey(key);
        comment.setContent(dto.getContent());

        comment.setCreatedAt(Instant.now());

        Comment saved = commentRepository.save(comment);
        return mapToResponse(saved);
    }

    public CommentResponseTo updateComment(@Valid CommentRequestTo dto) {

        Comment comment = commentRepository.findByKeyId(dto.getId()).orElseThrow(() -> new RuntimeException("Cannot update: Comment not found"));

        comment.setContent(dto.getContent());
        comment.setCreatedAt(Instant.now());

        Comment updated = commentRepository.save(comment);
        return mapToResponse(updated);
    }

    public void deleteComment(Long id) {
        Comment comment = commentRepository.findByKeyId(id).orElseThrow(()-> new RuntimeException("comment-not-found"));
        commentRepository.delete(comment);
    }

    private CommentResponseTo mapToResponse(Comment entity) {
        CommentResponseTo res = new CommentResponseTo();
        res.setId(entity.getKey().getId());
        res.setIssueId(entity.getKey().getIssueId());
        res.setContent(entity.getContent());
        res.setCreated(LocalDateTime.ofInstant(entity.getCreatedAt(), ZoneId.systemDefault()));
        return res;
    }
}
