package org.polozkov.service.comment;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.polozkov.dto.comment.CommentRequestTo;
import org.polozkov.dto.comment.CommentResponseTo;
import org.polozkov.entity.comment.Comment;
import org.polozkov.entity.issue.Issue;
import org.polozkov.exception.NotFoundException;
import org.polozkov.mapper.comment.CommentMapper;
import org.polozkov.repository.comment.CommentRepository;
import org.polozkov.service.issue.IssueService;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Service
@Validated
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final IssueService issueService;
    private final CommentMapper commentMapper;

    public List<CommentResponseTo> getAllComments() {
        return commentRepository.findAll().stream()
                .map(commentMapper::commentToResponseDto)
                .toList();
    }

    public CommentResponseTo getCommentById(Long id) {
        Comment comment = commentRepository.getById(id);
        return commentMapper.commentToResponseDto(comment);
    }

    public Comment getCommentEntityById(Long id) {
        return commentRepository.getById(id);
    }

    public CommentResponseTo createComment(@Valid CommentRequestTo commentRequest) {
        Issue issue = issueService.getIssueById(commentRequest.getIssueId());

        Comment comment = commentMapper.requestDtoToComment(commentRequest);

        Comment savedComment = commentRepository.save(comment);
        return commentMapper.commentToResponseDto(savedComment);
    }

    public CommentResponseTo updateComment(@Valid CommentRequestTo commentRequest) {
        commentRepository.getById(commentRequest.getId());

        Issue issue = issueService.getIssueById(commentRequest.getIssueId());

        Comment comment = getCommentEntityById(commentRequest.getId());
        comment = commentMapper.updateComment(comment, commentRequest);

        Comment updatedComment = commentRepository.update(comment);
        return commentMapper.commentToResponseDto(updatedComment);
    }

    public void deleteComment(Long id) {
        commentRepository.getById(id);
        commentRepository.deleteById(id);
    }

    public void validateCommentExists(Long id) {
        if (!commentRepository.existsById(id)) {
            throw new NotFoundException("Comment not found with id: " + id);
        }
    }
}