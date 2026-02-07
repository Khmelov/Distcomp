package com.example.demo.service;

import com.example.demo.dto.requests.IssueRequestTo;
import com.example.demo.dto.responses.IssueResponseTo;
import com.example.demo.model.Issue;
import com.example.demo.repository.IssueRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IssueService {
    private final IssueRepository repository;
    private final EntityMapper mapper;

    public IssueService(IssueRepository repository, EntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public IssueResponseTo create(IssueRequestTo dto){
        Issue issue = mapper.toIssueEntity(dto);
        Issue saved = repository.save(issue);
        return mapper.toIssueResponse(saved);
    }

    public IssueResponseTo findById(Long id) {
        Issue entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Issue not found"));
        return mapper.toIssueResponse(entity);
    }

    public List<IssueResponseTo> findAll() {
        return repository.findAll().stream()
                .map(mapper::toIssueResponse)
                .toList();
    }

    public IssueResponseTo update(Long id, IssueRequestTo dto) {
        Issue entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Issue not found"));
        mapper.updateEntity(dto, entity);
        return mapper.toIssueResponse(repository.save(entity));
    }

    public void delete(Long id) {
        if(!repository.existsById(id)){
            throw new RuntimeException("Issue not found: " + id);
        }
        repository.deleteById(id);
    }
}
