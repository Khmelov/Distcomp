package com.example.demo.service;

import com.example.demo.dto.requests.IssueRequestTo;
import com.example.demo.dto.responses.IssueResponseTo;
import com.example.demo.model.Author;
import com.example.demo.model.Issue;
import com.example.demo.repository.AuthorRepository;
import com.example.demo.repository.IssueRepository;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;

@Service
public class IssueService {
    private final IssueRepository repository;
    private final EntityMapper mapper;
    private final AuthorRepository authorRepository;

    public IssueService(IssueRepository repository, EntityMapper mapper, AuthorRepository authorRepository) {
        this.repository = repository;
        this.mapper = mapper;
        this.authorRepository = authorRepository;
    }

    public IssueResponseTo create(IssueRequestTo dto){
        Author author = authorRepository.findById(dto.getAuthorId())
                .orElseThrow(() -> new RuntimeException("Author not found"));
        Issue issue = mapper.toEntity(dto);
        issue.setAuthor(author);
        Issue saved = repository.save(issue);
        return mapper.toIssueResponse(saved);
    }

    public IssueResponseTo findById(Long id) {
        Issue issue = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Issue not found: " + id));

        return mapper.toIssueResponse(issue);
    }

    public List<IssueResponseTo> findAll() {
        List<Issue> list = repository.findAll();
        return mapper.toIssueResponseList(list);

    }

    public IssueResponseTo update(Long id, IssueRequestTo dto) {
        Issue existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Issue not found: " + id));

        mapper.updateIssue(dto, existing);
        Issue updated = repository.save(existing);
        return mapper.toIssueResponse(updated);
    }


    public void delete(Long id) {
        if(!repository.existsById(id)){
            throw new RuntimeException("Issue not found: " + id);
        }
        repository.deleteById(id);
    }
}
