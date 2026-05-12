package org.polozkov.service.issue;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.polozkov.dto.issue.IssueRequestTo;
import org.polozkov.dto.issue.IssueResponseTo;
import org.polozkov.entity.issue.Issue;
import org.polozkov.entity.label.Label;
import org.polozkov.entity.user.User;
import org.polozkov.exception.BadRequestException;
import org.polozkov.exception.ForbiddenException;
import org.polozkov.mapper.issue.IssueMapper;
import org.polozkov.repository.issue.IssueRepository;
import org.polozkov.repository.label.LabelRepository;
import org.polozkov.service.user.UserService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
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

    @Cacheable(value = "issues_list")
    public List<IssueResponseTo> getAllIssues() {
        return issueRepository.findAll().stream()
                .map(issueMapper::issueToResponseDto)
                .toList();
    }

    @Cacheable(value = "issues", key = "#id")
    public IssueResponseTo getIssue(Long id) {
        return issueMapper.issueToResponseDto(getIssueById(id));
    }

    public Issue getIssueById(Long id) {
        return issueRepository.byId(id);
    }

    @CacheEvict(value = "issues_list", allEntries = true)
    public IssueResponseTo createIssue(@Valid IssueRequestTo issueRequest) {
        User user = userService.getUserById(issueRequest.getUserId());

        if (issueRepository.findByTitle(issueRequest.getTitle()).isPresent()) {
            throw new ForbiddenException("Issue with title " + issueRequest.getTitle() + " already exists");
        }

        Issue issue = issueMapper.requestDtoToIssue(issueRequest);
        issue.setCreated(LocalDateTime.now());
        issue.setModified(LocalDateTime.now());
        issue.setUser(user);

        processLabels(issue, issueRequest.getLabels());

        Issue savedIssue = issueRepository.save(issue);
        return issueMapper.issueToResponseDto(savedIssue);
    }

    @Caching(
            put = @CachePut(value = "issues", key = "#issueRequest.id"),
            evict = @CacheEvict(value = "issues_list", allEntries = true)
    )
    public IssueResponseTo updateIssue(@Valid IssueRequestTo issueRequest) {
        Issue existingIssue = issueRepository.byId(issueRequest.getId());
        User user = userService.getUserById(issueRequest.getUserId());

        Issue issue = issueMapper.updateIssue(existingIssue, issueRequest);
        issue.setModified(LocalDateTime.now());
        issue.setCreated(existingIssue.getCreated());
        issue.setUser(user);

        processLabels(issue, issueRequest.getLabels());

        Issue updatedIssue = issueRepository.save(issue);
        return issueMapper.issueToResponseDto(updatedIssue);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "issues", key = "#id"),
            @CacheEvict(value = "issues_list", allEntries = true)
    })
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

    // Вынес повторяющуюся логику с метками в отдельный метод
    private void processLabels(Issue issue, List<String> labelNames) {
        List<Label> labels = new ArrayList<>();
        if (labelNames != null) {
            for (String name : labelNames) {
                Label label = labelRepository.findByName(name)
                        .orElseGet(() -> {
                            Label newLabel = new Label();
                            newLabel.setName(name);
                            newLabel.setIssues(new ArrayList<>());
                            return labelRepository.save(newLabel);
                        });
                labels.add(label);
            }
        }
        issue.setLabels(labels);
    }
}