package com.rest.restapp.service;

import com.rest.restapp.dto.request.IssueRequestToDto;
import com.rest.restapp.dto.response.AuthorResponseToDto;
import com.rest.restapp.dto.response.IssueResponseToDto;
import com.rest.restapp.entity.Issue;
import com.rest.restapp.exception.NotFoundException;
import com.rest.restapp.mapper.IssueMapper;
import com.rest.restapp.repositry.InMemoryRepository;
import jakarta.validation.ValidationException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class IssueService {

    InMemoryRepository repository;
    IssueMapper mapper;
    AuthorService authorService;

    @Transactional
    public IssueResponseToDto createIssue(IssueRequestToDto requestTo) {
        validateIssueRequest(requestTo);
        var author = repository.findAuthorById(requestTo.authorId())
                .orElseThrow(() -> new NotFoundException("Author with id " + requestTo.authorId() + " not found"));

        var issue = mapper.toEntity(requestTo);
        issue.setAuthor(author);
        Issue savedIssue = repository.saveIssue(issue);
        return mapper.toResponseTo(savedIssue);
    }

    public IssueResponseToDto getIssueById(Long id) {
        var issue = repository.findIssueById(id)
                .orElseThrow(() -> new NotFoundException("Issue with id " + id + " not found"));
        return mapper.toResponseTo(issue);
    }

    @Transactional(readOnly = true)
    public List<IssueResponseToDto> getAllIssues() {
        return repository.findAllIssues().stream()
                .map(mapper::toResponseTo)
                .toList();
    }

    @Transactional
    public IssueResponseToDto updateIssue(Long id, IssueRequestToDto requestTo) {
        validateIssueRequest(requestTo);
        var existingIssue = repository.findIssueById(id)
                .orElseThrow(() -> new NotFoundException("Issue with id " + id + " not found"));

        var author = repository.findAuthorById(requestTo.authorId())
                .orElseThrow(() -> new NotFoundException("Author with id " + requestTo.authorId() + " not found"));

        mapper.updateEntityFromDto(requestTo, existingIssue);
        existingIssue.setAuthor(author);
        Issue updatedIssue = repository.saveIssue(existingIssue);
        return mapper.toResponseTo(updatedIssue);
    }

    @Transactional
    public void deleteIssue(Long id) {
        if (!repository.existsIssueById(id)) {
            throw new NotFoundException("Issue with id " + id + " not found");
        }
        repository.deleteIssueById(id);
    }

    @Transactional(readOnly = true)
    public AuthorResponseToDto getAuthorByIssueId(Long issueId) {
        var issue = repository.findIssueById(issueId)
                .orElseThrow(() -> new NotFoundException("Issue with id " + issueId + " not found"));
        return authorService.getAuthorById(issue.getAuthor().getId());
    }

    private void validateIssueRequest(IssueRequestToDto requestTo) {
        if (requestTo == null) {
            throw new ValidationException("Issue request cannot be null");
        }
        if (requestTo.authorId() == null) {
            throw new ValidationException("Author ID is required");
        }
        if (requestTo.title() == null || requestTo.title().trim().isEmpty()) {
            throw new ValidationException("Title is required");
        }
        if (requestTo.content() == null || requestTo.content().trim().isEmpty()) {
            throw new ValidationException("Content is required");
        }
    }
}