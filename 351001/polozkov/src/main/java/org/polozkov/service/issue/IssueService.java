package org.polozkov.service.issue;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.polozkov.dto.issue.IssueRequestTo;
import org.polozkov.dto.issue.IssueResponseTo;
import org.polozkov.entity.issue.Issue;
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

        Issue savedIssue = issueRepository.save(issue);
        return issueMapper.issueToResponseDto(savedIssue);
    }

    public IssueResponseTo updateIssue(@Valid IssueRequestTo issueRequest) {
        issueRepository.getById(issueRequest.getId());

        User user = userService.getUserById(issueRequest.getUserId());

        Issue issue = getIssueById(issueRequest.getId());
        issue.setModified(LocalDateTime.now());
        issue = issueMapper.updateIssue(issue, issueRequest);

        Issue updatedIssue = issueRepository.update(issue);
        return issueMapper.issueToResponseDto(updatedIssue);
    }

    public void deleteIssue(Long id) {
        issueRepository.getById(id);
        issueRepository.deleteById(id);
    }
}