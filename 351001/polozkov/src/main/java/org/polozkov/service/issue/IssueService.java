package org.polozkov.service.issue;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.polozkov.dto.issue.IssueRequestTo;
import org.polozkov.dto.issue.IssueResponseTo;
import org.polozkov.entity.comment.Comment;
import org.polozkov.entity.issue.Issue;
import org.polozkov.entity.label.Label;
import org.polozkov.entity.user.User;
import org.polozkov.exception.BadRequestException;
import org.polozkov.exception.ForbiddenException;
import org.polozkov.mapper.issue.IssueMapper;
import org.polozkov.repository.issue.IssueRepository;
import org.polozkov.repository.label.LabelRepository;
import org.polozkov.service.user.UserService;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Validated
@RequiredArgsConstructor
public class IssueService {

    private final IssueRepository issueRepository;
    private final UserService userService;
    private final IssueMapper issueMapper;
    private final LabelRepository labelRepository;

    public List<IssueResponseTo> getAllIssues() {
        return issueRepository.findAll().stream()
                .map(issueMapper::issueToResponseDto)
                .toList();
    }

    public IssueResponseTo getIssue(Long id) {
        return issueMapper.issueToResponseDto(getIssueById(id));
    }

    public Issue getIssueById(Long id) {
        return issueRepository.byId(id);
    }

    public IssueResponseTo createIssue(@Valid IssueRequestTo issueRequest) {
        User user = userService.getUserById(issueRequest.getUserId());

        if (issueRepository.findByTitle(issueRequest.getTitle()).isPresent()) {
            throw new ForbiddenException("Issue with title " + issueRequest.getTitle() + " already exists");
        }

        Issue issue = issueMapper.requestDtoToIssue(issueRequest);
        issue.setCreated(LocalDateTime.now());
        issue.setModified(LocalDateTime.now());

        issue.setUser(user);

        issue.setComments(new ArrayList<>());

        List<Label> labels = new ArrayList<>();
        if (issueRequest.getLabels() != null) {
            for (String labelName : issueRequest.getLabels()) {
                Label label = labelRepository.findByName(labelName)
                        .orElseGet(() -> {
                            Label newLabel = new Label();
                            newLabel.setName(labelName);
                            newLabel.setIssues(new ArrayList<>());
                            return labelRepository.save(newLabel);
                        });
                labels.add(label);
            }
        }
        issue.setLabels(labels);

        Issue savedIssue = issueRepository.save(issue);
        return issueMapper.issueToResponseDto(savedIssue);
    }

    public IssueResponseTo updateIssue(@Valid IssueRequestTo issueRequest) {
        Issue existingIssue = issueRepository.byId(issueRequest.getId());

        User user = userService.getUserById(issueRequest.getUserId());

        Issue issue = issueMapper.updateIssue(existingIssue, issueRequest);
        issue.setModified(LocalDateTime.now());

        issue.setCreated(existingIssue.getCreated());

        issue.setUser(user);

        issue.setComments(existingIssue.getComments());

        List<Label> labels = new ArrayList<>();
        if (issueRequest.getLabels() != null) {
            for (String labelName : issueRequest.getLabels()) {
                Label label = labelRepository.findByName(labelName)
                        .orElseGet(() -> {
                            Label newLabel = new Label();
                            newLabel.setName(labelName);
                            newLabel.setIssues(new ArrayList<>());
                            return labelRepository.save(newLabel);
                        });
                labels.add(label);
            }
        }
        issue.setLabels(labels);

        Issue updatedIssue = issueRepository.save(issue);
        return issueMapper.issueToResponseDto(updatedIssue);
    }

    @Transactional
    public void deleteIssue(Long id) {
        Issue issue = issueRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Issue not found"));

        List<Label> labelsToCheck = new ArrayList<>(issue.getLabels());

        issue.getLabels().clear();
        issueRepository.delete(issue);

        issueRepository.flush();

        for (Label label : labelsToCheck) {
            if (labelRepository.countIssuesByLabelId(label.getId()) == 0) {
                labelRepository.delete(label);
            }
        }
    }

    public void addCommentToIssue(Long issueId, Comment comment) {
        Issue issue = issueRepository.byId(issueId);
        issue.getComments().add(comment);
        issueRepository.save(issue);
    }

    public void addLabelToIssue(Long issueId, Label label) {
        Issue issue = issueRepository.byId(issueId);
        issue.getLabels().add(label);
        issueRepository.save(issue);
    }
}