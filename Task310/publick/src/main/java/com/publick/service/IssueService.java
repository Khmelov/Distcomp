package com.publick.service;

import com.publick.dto.IssueRequestTo;
import com.publick.dto.IssueResponseTo;
import com.publick.entity.Author;
import com.publick.entity.Issue;
import com.publick.repository.AuthorRepository;
import com.publick.repository.IssueRepository;
import com.publick.service.mapper.IssueMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class IssueService {

    @Autowired
    private IssueRepository issueRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private IssueMapper issueMapper;

    public IssueResponseTo create(IssueRequestTo request) {
        // Validate that author exists
        if (!authorRepository.existsById(request.getAuthorId())) {
            throw new IllegalArgumentException("Author not found with id: " + request.getAuthorId());
        }

        Issue issue = issueMapper.toEntity(request);
        LocalDateTime now = LocalDateTime.now();
        issue.setCreated(now);
        issue.setModified(now);
        Issue saved = issueRepository.save(issue);
        return issueMapper.toResponse(saved);
    }

    public IssueResponseTo getById(Long id) {
        Issue issue = issueRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Issue not found with id: " + id));
        return issueMapper.toResponse(issue);
    }

    public List<IssueResponseTo> getAll() {
        return issueRepository.findAll().stream()
                .map(issueMapper::toResponse)
                .collect(Collectors.toList());
    }

    public IssueResponseTo update(Long id, IssueRequestTo request) {
        Issue existing = issueRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Issue not found with id: " + id));

        // Validate that author exists
        if (!authorRepository.existsById(request.getAuthorId())) {
            throw new IllegalArgumentException("Author not found with id: " + request.getAuthorId());
        }

        issueMapper.updateEntityFromDto(request, existing);
        existing.setModified(LocalDateTime.now());
        Issue saved = issueRepository.update(existing);
        return issueMapper.toResponse(saved);
    }

    public void delete(Long id) {
        if (!issueRepository.existsById(id)) {
            throw new IllegalArgumentException("Issue not found with id: " + id);
        }
        issueRepository.deleteById(id);
    }
}