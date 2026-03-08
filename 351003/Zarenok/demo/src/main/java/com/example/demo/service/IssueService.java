package com.example.demo.service;

import com.example.demo.dto.requests.IssueRequestTo;
import com.example.demo.dto.responses.AuthorResponseTo;
import com.example.demo.dto.responses.IssueResponseTo;
import com.example.demo.exception.DuplicateException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.model.Author;
import com.example.demo.model.Issue;
import com.example.demo.model.Mark;
import com.example.demo.repository.AuthorRepository;
import com.example.demo.repository.IssueRepository;
import com.example.demo.repository.MarkRepository;
import com.example.demo.specification.IssueSpecifications;
import jakarta.transaction.Transactional;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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
    private final MarkRepository markRepository;

    public IssueService(IssueRepository repository, EntityMapper mapper,
                        AuthorRepository authorRepository, MarkRepository markRepository) {
        this.repository = repository;
        this.mapper = mapper;
        this.authorRepository = authorRepository;
        this.markRepository = markRepository;
    }

    public IssueResponseTo create(IssueRequestTo dto) {
        Author author = authorRepository.findById(dto.getAuthorId())
                .orElseThrow(() -> new NotFoundException("Author not found"));

        if (repository.existsByTitle(dto.getTitle())) {
            throw new DuplicateException("Issue with this title already exists");
        }


        Issue issue = mapper.toEntity(dto);
        issue.setAuthor(author);

        if (dto.getMarks() != null && !dto.getMarks().isEmpty()) {
            List<Mark> marks = dto.getMarks().stream()
                    .map(name -> markRepository.findByName(name)
                            .orElseGet(() -> {
                                // Если метка не существует, создаем новую
                                Mark newMark = new Mark();
                                newMark.setName(name);
                                return markRepository.save(newMark);
                            }))
                    .collect(Collectors.toList());
            issue.setMarks(marks);
        }

        Issue saved = repository.save(issue);
        return mapper.toIssueResponse(saved);
    }

    public IssueResponseTo findById(Long id) {
        Issue issue = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Issue not found"));

        return mapper.toIssueResponse(issue);
    }

    public List<IssueResponseTo> findAll(String title, String content, Long authorId, String markName) {
        Specification<Issue> spec = IssueSpecifications.withFilters(title, content, authorId, markName);
        return repository.findAll(spec).stream()
                .map(mapper::toIssueResponse)
                .collect(Collectors.toList());
    }

    public Page<IssueResponseTo> findAll(Pageable pageable, String title, String content, Long authorId, String markName) {
        Specification<Issue> spec = IssueSpecifications.withFilters(title, content, authorId, markName);
        return repository.findAll(spec, pageable)
                .map(mapper::toIssueResponse);
    }

    public IssueResponseTo update(Long id, IssueRequestTo dto) {
        Issue existing = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Issue not found"));

        if (!existing.getTitle().equals(dto.getTitle()) &&
                repository.existsByTitle(dto.getTitle())) {
            throw new DuplicateException("Issue with this title already exists");
        }

        if (!existing.getAuthor().getId().equals(dto.getAuthorId())) {
            Author newAuthor = authorRepository.findById(dto.getAuthorId())
                    .orElseThrow(() -> new NotFoundException("Author not found"));
            existing.setAuthor(newAuthor);
        }

        if (dto.getMarks() != null) {
            List<Mark> marks = dto.getMarks().stream()
                    .map(name -> markRepository.findByName(name)
                            .orElseGet(() -> {
                                Mark newMark = new Mark();
                                newMark.setName(name);
                                return markRepository.save(newMark);
                            }))
                    .collect(Collectors.toList());
            existing.setMarks(marks);
        }


        mapper.updateIssue(dto, existing);
        Issue updated = repository.save(existing);
        return mapper.toIssueResponse(updated);
    }


    public void delete(Long id) {
        Issue issue = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Issue not found"));

        List<Mark> marks = issue.getMarks();
        repository.delete(issue);

        for (Mark mark : marks) {
            if (repository.countIssuesByMarkId(mark.getId()) == 0) {
                markRepository.delete(mark);
            }
        }
    }
}
