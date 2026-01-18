package com.rest.restapp.service;

import com.rest.restapp.dto.request.IssueRequestTo;
import com.rest.restapp.dto.response.UserResponseTo;
import com.rest.restapp.dto.response.IssueResponseTo;
import com.rest.restapp.entity.Issue;
import com.rest.restapp.entity.Tag;
import com.rest.restapp.exception.DuplicateException;
import com.rest.restapp.exception.NotFoundException;
import com.rest.restapp.mapper.IssueMapper;
import com.rest.restapp.repository.UserRepository;
import com.rest.restapp.repository.IssueRepository;
import com.rest.restapp.repository.TagRepository;
import jakarta.validation.ValidationException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class IssueService {

    IssueRepository issueRepository;
    UserRepository userRepository;
    TagRepository tagRepository;
    IssueMapper mapper;
    UserService userService;

    @Transactional
    public IssueResponseTo createIssue(IssueRequestTo requestTo) {
        validateIssueRequest(requestTo);
        if (issueRepository.existsByTitle(requestTo.title())) {
            throw new DuplicateException("This issue already exists");
        }

        var user = userRepository
                .findById(requestTo.userId())
                .orElseThrow(() -> new NotFoundException("User with id " + requestTo.userId() + " not found"));

        var issue = mapper.toEntity(requestTo);
        issue.setUser(user);

        if(requestTo.tags() != null) {
            var tags = requestTo.tags().stream()
                    .map(tagName -> tagRepository
                            .findByName(tagName)
                            .orElseGet(() -> tagRepository.save(new Tag(null, tagName, null))))
                    .toList();
            issue.setTags(tags);
        }

        Issue savedIssue = issueRepository.save(issue);
        return mapper.toResponseTo(savedIssue);
    }

    public IssueResponseTo getIssueById(Long id) {
        var issue = issueRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException("Issue with id " + id + " not found"));
        return mapper.toResponseTo(issue);
    }

    @Transactional(readOnly = true)
    public List<IssueResponseTo> getAllIssues() {
        return issueRepository.findAll().stream()
                .map(mapper::toResponseTo)
                .toList();
    }

    @Transactional
    public IssueResponseTo updateIssue(Long id, IssueRequestTo requestTo) {
        validateIssueRequest(requestTo);
        var existingIssue = issueRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Issue with id " + id + " not found"));

        var user = userRepository.findById(requestTo.userId())
                .orElseThrow(() -> new NotFoundException("User with id " + requestTo.userId() + " not found"));

        mapper.updateEntityFromDto(requestTo, existingIssue);
        existingIssue.setUser(user);
        var updatedIssue = issueRepository.save(existingIssue);
        return mapper.toResponseTo(updatedIssue);
    }

    @Transactional
    public void deleteIssue(Long id) {
        var issue = issueRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException("Issue with id " + id + " not found"));

        var tags = new ArrayList<>(issue.getTags());

        issue.getTags().clear();
        issueRepository.delete(issue);

        for(Tag tag : tags) {
            tag.getIssues().remove(issue);
            if(tag.getIssues().isEmpty()) {
                tagRepository.delete(tag);
            }
        }
        issueRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public UserResponseTo getUserByIssueId(Long issueId) {
        var issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new NotFoundException("Issue with id " + issueId + " not found"));
        return userService.getUserById(issue.getUser().getId());
    }

    private void validateIssueRequest(IssueRequestTo requestTo) {
        if (requestTo == null) {
            throw new ValidationException("Issue request cannot be null");
        }
        if (requestTo.userId() == null) {
            throw new ValidationException("User ID is required");
        }
        if (requestTo.title() == null || requestTo.title().trim().isEmpty()) {
            throw new ValidationException("Title is required");
        }
        if (requestTo.content() == null || requestTo.content().trim().isEmpty()) {
            throw new ValidationException("Content is required");
        }
    }
}