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

    public CommentResponseTo getComment(Long id) {
        Comment comment = commentRepository.byId(id);
        return commentMapper.commentToResponseDto(comment);
    }

    public Comment getCommentById(Long id) {
        return commentRepository.byId(id);
    }

    public CommentResponseTo createComment(@Valid CommentRequestTo commentRequest) {
        Issue issue = issueService.getIssueById(commentRequest.getIssueId());

        Comment comment = commentMapper.requestDtoToComment(commentRequest);

        comment.setIssue(issue);


        Comment savedComment = commentRepository.save(comment);

        issueService.addCommentToIssue(issue.getId(), savedComment);

        return commentMapper.commentToResponseDto(savedComment);
    }

    public CommentResponseTo updateComment(@Valid CommentRequestTo commentRequest) {
        Comment existingComment = commentRepository.byId(commentRequest.getId());

        Issue issue;
        if (!existingComment.getIssue().getId().equals(commentRequest.getIssueId())) {
            issue = issueService.getIssueById(commentRequest.getIssueId());
        } else {
            issue = existingComment.getIssue();
        }

        Comment comment = getCommentById(commentRequest.getId());
        comment = commentMapper.updateComment(comment, commentRequest);

        Comment updatedComment = commentRepository.save(comment);

        if (!existingComment.getIssue().getId().equals(commentRequest.getIssueId())) {
            existingComment.getIssue().getComments().removeIf(c -> c.getId().equals(commentRequest.getId()));

            issueService.addCommentToIssue(issue.getId(), updatedComment);
        }

        return commentMapper.commentToResponseDto(updatedComment);
    }

    public void deleteComment(Long id) {
        Comment comment = commentRepository.byId(id);

        comment.getIssue().getComments().removeIf(c -> c.getId().equals(id));

        commentRepository.deleteById(id);
    }

}