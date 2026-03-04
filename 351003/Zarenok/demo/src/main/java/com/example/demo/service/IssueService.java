package com.example.demo.service;

import com.example.demo.dto.requests.IssueRequestTo;
import com.example.demo.dto.responses.AuthorResponseTo;
import com.example.demo.dto.responses.IssueResponseTo;
import com.example.demo.exception.DuplicateException;
import com.example.demo.exception.NotFoundException;
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
import java.util.stream.Collectors;

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
                .orElseThrow(() -> new NotFoundException("Author not found"));
        if (repository.existsByTitle(dto.getTitle())) {
            throw new DuplicateException("Issue with this title already exists");
        }

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

    public List<IssueResponseTo> findAll() {
        return repository.findAll().stream()
                .map(mapper::toIssueResponse)
                .collect(Collectors.toList());
    }

    public IssueResponseTo update(Long id, IssueRequestTo dto)
            throws ChangeSetPersister.NotFoundException {

        if (repository.existsByTitle(dto.getTitle())) {
            throw new DuplicateException("Issue with this title already exists");
        }
        Issue existing = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Issue not found"));

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
