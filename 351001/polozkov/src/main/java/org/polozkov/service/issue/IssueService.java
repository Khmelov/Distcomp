package org.polozkov.service.issue;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.polozkov.dto.issue.IssueRequestTo;
import org.polozkov.dto.issue.IssueResponseTo;
import org.polozkov.entity.comment.Comment;
import org.polozkov.entity.issue.Issue;
import org.polozkov.entity.label.Label;
import org.polozkov.entity.user.User;
import org.polozkov.mapper.issue.IssueMapper;
import org.polozkov.repository.issue.IssueRepository;
import org.polozkov.service.user.UserService;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Validated
@RequiredArgsConstructor
public class IssueService {

    private final IssueRepository issueRepository;
    private final UserService userService;
    private final IssueMapper issueMapper;

    public List<IssueResponseTo> getAllIssues() {
        return issueRepository.findAll().stream()
                .map(issueMapper::issueToResponseDto)
                .toList();
    }

    public IssueResponseTo getIssue(Long id) {
        return issueMapper.issueToResponseDto(getIssueById(id));
    }

    public Issue getIssueById(Long id) {
        return issueRepository.getById(id);
    }

    public IssueResponseTo createIssue(@Valid IssueRequestTo issueRequest) {
        User user = userService.getUserById(issueRequest.getUserId());

        Issue issue = issueMapper.requestDtoToIssue(issueRequest);
        issue.setCreated(LocalDateTime.now());
        issue.setModified(LocalDateTime.now());

        issue.setUser(user);

        issue.setComments(List.of());
        issue.setLabels(List.of());

        Issue savedIssue = issueRepository.save(issue);
        return issueMapper.issueToResponseDto(savedIssue);
    }

    public IssueResponseTo updateIssue(@Valid IssueRequestTo issueRequest) {
        Issue existingIssue = issueRepository.getById(issueRequest.getId());

        User user = userService.getUserById(issueRequest.getUserId());

        Issue issue = issueMapper.updateIssue(existingIssue, issueRequest);
        issue.setModified(LocalDateTime.now());

        issue.setCreated(existingIssue.getCreated());

        issue.setUser(user);

        issue.setComments(existingIssue.getComments());
        issue.setLabels(existingIssue.getLabels());

        Issue updatedIssue = issueRepository.update(issue);
        return issueMapper.issueToResponseDto(updatedIssue);
    }

    public void deleteIssue(Long id) {
        issueRepository.getById(id);
        issueRepository.deleteById(id);
    }

    public void addCommentToIssue(Long issueId, Comment comment) {
        Issue issue = issueRepository.getById(issueId);
        issue.getComments().add(comment);
        issueRepository.update(issue);
    }

    public void addLabelToIssue(Long issueId, Label label) {
        Issue issue = issueRepository.getById(issueId);
        issue.getLabels().add(label);
        issueRepository.update(issue);
    }
}