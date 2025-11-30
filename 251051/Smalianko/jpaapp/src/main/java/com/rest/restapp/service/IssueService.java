package com.rest.restapp.service;

import com.rest.restapp.dto.request.IssueRequestToDto;
import com.rest.restapp.dto.response.AuthorResponseToDto;
import com.rest.restapp.dto.response.IssueResponseToDto;
import com.rest.restapp.entity.Issue;
import com.rest.restapp.entity.Tag;
import com.rest.restapp.exception.DuplicateException;
import com.rest.restapp.exception.NotFoundException;
import com.rest.restapp.mapper.IssueMapper;
import com.rest.restapp.repositry.AuthorRepository;
import com.rest.restapp.repositry.IssueRepository;
import com.rest.restapp.repositry.TagRepository;
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
    AuthorRepository authorRepository;
    TagRepository tagRepository;
    IssueMapper mapper;
    AuthorService authorService;

    @Transactional
    public IssueResponseToDto createIssue(IssueRequestToDto requestTo) {
        validateIssueRequest(requestTo);
        if (issueRepository.existsByTitle(requestTo.title())) {
            throw new DuplicateException("This issue already exists");
        }

        var author = authorRepository.findById(requestTo.authorId())
                .orElseThrow(() -> new NotFoundException("Author with id " + requestTo.authorId() + " not found"));

        var issue = mapper.toEntity(requestTo);
        issue.setAuthor(author);

        if(requestTo.tags() != null) {
            var tags = requestTo.tags().stream()
                    .map(tagName -> tagRepository.findByName(tagName)
                            .orElseGet(() -> tagRepository.save(new Tag(null, tagName, null))))
                    .toList();
            issue.setTags(tags);
        }

        Issue savedIssue = issueRepository.save(issue);
        return mapper.toResponseTo(savedIssue);
    }

    public IssueResponseToDto getIssueById(Long id) {
        var issue = issueRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Issue with id " + id + " not found"));
        return mapper.toResponseTo(issue);
    }

    @Transactional(readOnly = true)
    public List<IssueResponseToDto> getAllIssues() {
        return issueRepository.findAll().stream()
                .map(mapper::toResponseTo)
                .toList();
    }

    @Transactional
    public IssueResponseToDto updateIssue(Long id, IssueRequestToDto requestTo) {
        validateIssueRequest(requestTo);
        var existingIssue = issueRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Issue with id " + id + " not found"));

        var author = authorRepository.findById(requestTo.authorId())
                .orElseThrow(() -> new NotFoundException("Author with id " + requestTo.authorId() + " not found"));

        mapper.updateEntityFromDto(requestTo, existingIssue);
        existingIssue.setAuthor(author);
        var updatedIssue = issueRepository.save(existingIssue);
        return mapper.toResponseTo(updatedIssue);
    }

    @Transactional
    public void deleteIssue(Long id) {
        var issue = issueRepository.findById(id)
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
    public AuthorResponseToDto getAuthorByIssueId(Long issueId) {
        var issue = issueRepository.findById(issueId)
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