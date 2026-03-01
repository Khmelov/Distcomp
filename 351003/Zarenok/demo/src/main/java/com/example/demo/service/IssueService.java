package com.example.demo.service;

import com.example.demo.dto.requests.IssueRequestTo;
import com.example.demo.dto.responses.IssueResponseTo;
import com.example.demo.model.Author;
import com.example.demo.model.Issue;
import com.example.demo.repository.AuthorRepository;
import com.example.demo.repository.IssueRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;

@Service
@Transactional
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
                .orElseThrow(() -> new NotFoundException("Author not found"));
        Issue issue = mapper.toEntity(dto);
        issue.setAuthor(author);
        Issue saved = repository.save(issue);
        return mapper.toIssueResponse(saved);
    }

    public IssueResponseTo findById(Long id) {
        Issue issue = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Issue not found: " + id));

        return mapper.toIssueResponse(issue);
    }

    public Page<IssueResponseTo> findAll(Pageable pageable) {
        return repository.findAll(pageable)
                .map(mapper::toIssueResponse);
    }

    public IssueResponseTo update(Long id, IssueRequestTo dto) {
        Issue existing = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Issue not found: " + id));

        mapper.updateIssue(dto, existing);
        Issue updated = repository.save(existing);
        return mapper.toIssueResponse(updated);
    }


    public void delete(Long id) {
        if(!repository.existsById(id)){
            throw new NotFoundException("Issue not found: " + id);
        }
        repository.deleteById(id);
    }
}
