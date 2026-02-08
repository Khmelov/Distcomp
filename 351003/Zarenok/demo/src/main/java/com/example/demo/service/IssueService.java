package com.example.demo.service;

import com.example.demo.dto.requests.IssueRequestTo;
import com.example.demo.dto.responses.IssueResponseTo;
import com.example.demo.model.Issue;
import com.example.demo.repository.IssueRepository;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;

@Service
public class IssueService {
    private final IssueRepository repository;

    public IssueService(IssueRepository repository) {
        this.repository = repository;
    }

    public IssueResponseTo create(IssueRequestTo dto){
        Issue issue = new Issue();
        issue.setAuthorId(dto.getAuthorId());
        issue.setTitle(dto.getTitle());
        issue.setContent(dto.getContent());
        issue.setCreated(ZonedDateTime.now());
        issue.setModified(ZonedDateTime.now());
        Issue saved = repository.save(issue);

        return new IssueResponseTo(
                saved.getId(),
                saved.getAuthorId(),
                saved.getTitle(),
                saved.getContent(),
                saved.getCreated(),
                saved.getModified()
        );
    }

    public IssueResponseTo findById(Long id) {
        Issue issue = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Issue not found: " + id));

        return new IssueResponseTo(
                issue.getId(),
                issue.getAuthorId(),
                issue.getTitle(),
                issue.getContent(),
                issue.getCreated(),
                issue.getModified()
        );
    }

    public List<IssueResponseTo> findAll() {
        return repository.findAll().stream()
                .map(issue -> new IssueResponseTo(
                        issue.getId(),
                        issue.getAuthorId(),
                        issue.getTitle(),
                        issue.getContent(),
                        issue.getCreated(),
                        issue.getModified()
                ))
                .toList();
    }

    public IssueResponseTo update(Long id, IssueRequestTo dto) {
        Issue existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Issue not found: " + id));

        existing.setAuthorId(dto.getAuthorId());
        existing.setTitle(dto.getTitle());
        existing.setContent(dto.getContent());
        existing.setModified(ZonedDateTime.now());

        Issue updated = repository.save(existing);

        return new IssueResponseTo(
                updated.getId(),
                updated.getAuthorId(),
                updated.getTitle(),
                updated.getContent(),
                updated.getCreated(),
                updated.getModified()
        );
    }


    public void delete(Long id) {
        if(!repository.existsById(id)){
            throw new RuntimeException("Issue not found: " + id);
        }
        repository.deleteById(id);
    }
}
