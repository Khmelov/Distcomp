package com.example.demo.service;

import com.example.demo.dto.requests.IssueRequestTo;
import com.example.demo.dto.responses.IssueResponseTo;
import com.example.demo.model.Author;
import com.example.demo.model.Issue;
import com.example.demo.repository.AuthorRepository;
import com.example.demo.repository.IssueRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.crossstore.ChangeSetPersister;
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

    public IssueResponseTo create(IssueRequestTo dto)
            throws ChangeSetPersister.NotFoundException {
        Author author = authorRepository.findById(dto.getAuthorId())
                .orElseThrow(ChangeSetPersister.NotFoundException::new);
        Issue issue = mapper.toEntity(dto);
        issue.setAuthor(author);
        Issue saved = repository.save(issue);
        return mapper.toIssueResponse(saved);
    }

    public IssueResponseTo findById(Long id)
            throws ChangeSetPersister.NotFoundException {
        Issue issue = repository.findById(id)
                .orElseThrow(ChangeSetPersister.NotFoundException::new);

        return mapper.toIssueResponse(issue);
    }

    public Page<IssueResponseTo> findAll(Pageable pageable) {
        return repository.findAll(pageable)
                .map(mapper::toIssueResponse);
    }

    public IssueResponseTo update(Long id, IssueRequestTo dto)
            throws ChangeSetPersister.NotFoundException {
        Issue existing = repository.findById(id)
                .orElseThrow(ChangeSetPersister.NotFoundException::new);

        mapper.updateIssue(dto, existing);
        Issue updated = repository.save(existing);
        return mapper.toIssueResponse(updated);
    }


    public void delete(Long id) throws ChangeSetPersister.NotFoundException {
        if(!repository.existsById(id)){
            throw new ChangeSetPersister.NotFoundException();
        }
        repository.deleteById(id);
    }
}
