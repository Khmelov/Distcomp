package org.polozkov.service.comment;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.polozkov.dto.comment.CommentRequestTo;
import org.polozkov.dto.comment.CommentResponseTo;
import org.polozkov.entity.comment.Comment;
import org.polozkov.mapper.comment.CommentMapper;
import org.polozkov.repository.comment.CommentRepository;
import org.polozkov.repository.issue.IssueRepository;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Validated
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final IssueRepository issueRepository;
    private final CommentMapper commentMapper;

    public List<CommentResponseTo> getAllComments() {
        return commentRepository.findAll().stream()
                .map(commentMapper::commentToResponseDto)
                .collect(Collectors.toList());
    }

    public CommentResponseTo getCommentById(Long id) {
        return commentRepository.findById(id)
                .map(commentMapper::commentToResponseDto)
                .orElseThrow(() -> new RuntimeException("Comment not found with id: " + id));
    }

    public CommentResponseTo createComment(@Valid CommentRequestTo commentRequest) {
        if (!issueRepository.existsById(commentRequest.getIssueId())) {
            throw new RuntimeException("Issue not found with id: " + commentRequest.getIssueId());
        }

        Comment comment = commentMapper.requestDtoToComment(commentRequest);

        Comment savedComment = commentRepository.save(comment);
        return commentMapper.commentToResponseDto(savedComment);
    }

    public CommentResponseTo updateComment(@Valid CommentRequestTo commentRequest) {
        if (!commentRepository.existsById(commentRequest.getId())) {
            throw new RuntimeException("Comment not found with id: " + commentRequest.getId());
        }

        if (!issueRepository.existsById(commentRequest.getIssueId())) {
            throw new RuntimeException("Issue not found with id: " + commentRequest.getIssueId());
        }

        Comment comment = commentMapper.requestDtoToComment(commentRequest);

        Comment updatedComment = commentRepository.update(comment);
        return commentMapper.commentToResponseDto(updatedComment);
    }

    public void deleteComment(Long id) {
        if (!commentRepository.existsById(id)) {
            throw new RuntimeException("Comment not found with id: " + id);
        }
        commentRepository.deleteById(id);
    }
}