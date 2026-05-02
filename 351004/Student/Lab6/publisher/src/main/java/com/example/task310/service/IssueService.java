package com.example.task310.service;

import com.example.task310.dto.*;
import com.example.task310.entity.Issue;
import com.example.task310.mapper.EntityMapper;
import com.example.task310.repository.IssueRepository;
import com.example.task310.repository.WriterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IssueService {
    private final IssueRepository repository;
    private final EntityMapper mapper;
    private final WriterRepository writerRepository;

    public List<IssueResponseTo> getAll(Pageable pageable) {
        return mapper.toIssueResponseList(repository.findAll(pageable).getContent());
    }

    @Cacheable(value = "issues", key = "#id")
    public IssueResponseTo getById(Long id) {
        return repository.findById(id).map(mapper::toResponse)
                .orElseThrow(() -> new RuntimeException("Issue not found"));
    }

    public IssueResponseTo create(IssueRequestTo dto) {
        if (!writerRepository.existsById(dto.getWriterId())) {
            throw new RuntimeException("Writer not found");
        }
        Issue entity = mapper.toEntity(dto);
        entity.setCreated(LocalDateTime.now());
        entity.setModified(LocalDateTime.now());
        return mapper.toResponse(repository.save(entity));
    }

    @CacheEvict(value = "issues", key = "#dto.id")
    public IssueResponseTo update(IssueRequestTo dto) {
        Issue existing = repository.findById(dto.getId())
                .orElseThrow(() -> new RuntimeException("Issue not found"));

        Issue entity = mapper.toEntity(dto);
        entity.setCreated(existing.getCreated());
        entity.setModified(LocalDateTime.now());
        return mapper.toResponse(repository.save(entity));
    }

    @CacheEvict(value = "issues", key = "#id")
    public void delete(Long id) {
        if (!repository.existsById(id)) throw new RuntimeException("Issue not found");
        repository.deleteById(id);
    }
}