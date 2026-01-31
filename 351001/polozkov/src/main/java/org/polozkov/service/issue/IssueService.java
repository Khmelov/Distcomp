package org.polozkov.service.issue;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.polozkov.dto.issue.IssueRequestTo;
import org.polozkov.dto.issue.IssueResponseTo;
import org.polozkov.entity.issue.Issue;
import org.polozkov.exception.NotFoundException;
import org.polozkov.mapper.issue.IssueMapper;
import org.polozkov.repository.issue.IssueRepository;
import org.polozkov.repository.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Validated
@RequiredArgsConstructor
public class IssueService {

    private final IssueRepository issueRepository;
    private final UserRepository userRepository;
    private final IssueMapper issueMapper;

    public List<IssueResponseTo> getAllIssues() {
        return issueRepository.findAll().stream()
                .map(issueMapper::issueToResponseDto)
                .collect(Collectors.toList());
    }

    public IssueResponseTo getIssueById(Long id) {
        return issueRepository.findById(id)
                .map(issueMapper::issueToResponseDto)
                .orElseThrow(() -> new RuntimeException("Issue not found with id: " + id));
    }

    public IssueResponseTo createIssue(@Valid IssueRequestTo issueRequest) {
        if (!userRepository.existsById(issueRequest.getUserId())) {
            throw new RuntimeException("User not found with id: " + issueRequest.getUserId());
        }

        Issue issue = issueMapper.requestDtoToIssue(issueRequest);
        issue.setCreated(LocalDateTime.now());
        issue.setModified(LocalDateTime.now());

        Issue savedIssue = issueRepository.save(issue);
        return issueMapper.issueToResponseDto(savedIssue);
    }

    public IssueResponseTo updateIssue(@Valid IssueRequestTo issueRequest) {
        if (!issueRepository.existsById(issueRequest.getId())) {
            throw new RuntimeException("Issue not found with id: " + issueRequest.getId());
        }

        if (!userRepository.existsById(issueRequest.getUserId())) {
            throw new RuntimeException("User not found with id: " + issueRequest.getUserId());
        }

        Issue issue = issueRepository.findById(issueRequest.getId()).orElseThrow(() -> new NotFoundException("Issue not found with id: " + issueRequest.getId()));
        issue.setModified(LocalDateTime.now());
        issue = issueMapper.updateIssue(issue, issueRequest);

        Issue updatedIssue = issueRepository.update(issue);
        return issueMapper.issueToResponseDto(updatedIssue);
    }

    public void deleteIssue(Long id) {
        if (!issueRepository.existsById(id)) {
            throw new NotFoundException("Issue not found with id: " + id);
        }
        issueRepository.deleteById(id);
    }
}