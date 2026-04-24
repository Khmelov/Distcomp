package com.example.task310.service;

import com.example.task310.dto.IssueRequestTo;
import com.example.task310.dto.IssueResponseTo;
import com.example.task310.entity.Issue;
import com.example.task310.mapper.EntityMapper;
import com.example.task310.repository.IssueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IssueService {
    private final IssueRepository repository;
    private final EntityMapper mapper;

    public IssueResponseTo create(IssueRequestTo dto) {
        Issue entity = mapper.toEntity(dto);
        entity.setCreated(LocalDateTime.now());
        entity.setModified(LocalDateTime.now());
        return mapper.toResponse(repository.save(entity));
    }

    public IssueResponseTo getById(Long id) {
        return repository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new RuntimeException("Issue not found"));
    }

    public List<IssueResponseTo> getAll() {
        return mapper.toIssueResponseList(repository.findAll());
    }

    public IssueResponseTo update(IssueRequestTo dto) {
        Issue existing = repository.findById(dto.getId())
                .orElseThrow(() -> new RuntimeException("Issue not found"));

        Issue entity = mapper.toEntity(dto);
        entity.setCreated(existing.getCreated());
        entity.setModified(LocalDateTime.now());
        return mapper.toResponse(repository.save(entity));
    }

    public void delete(Long id) {
        if (repository.findById(id).isEmpty()) {
            throw new RuntimeException("Issue not found");
        }
        repository.deleteById(id);
    }
}