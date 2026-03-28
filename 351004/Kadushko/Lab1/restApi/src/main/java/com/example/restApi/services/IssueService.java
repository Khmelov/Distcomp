package com.example.restApi.services;

import com.example.restApi.dto.request.IssueRequestTo;
import com.example.restApi.dto.response.IssueResponseTo;
import com.example.restApi.exception.AlreadyExistsException;
import com.example.restApi.exception.NotFoundException;
import com.example.restApi.mapper.IssueMapper;
import com.example.restApi.model.Issue;
import com.example.restApi.repository.EditorRepository;
import com.example.restApi.repository.IssueRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class IssueService {

    private final IssueRepository issueRepository;
    private final EditorRepository editorRepository;
    private final IssueMapper issueMapper;

    public IssueService(IssueRepository issueRepository,
                        EditorRepository editorRepository,
                        IssueMapper issueMapper) {
        this.issueRepository = issueRepository;
        this.editorRepository = editorRepository;
        this.issueMapper = issueMapper;
    }

    public List<IssueResponseTo> getAll() {
        return issueRepository.findAll().stream()
                .map(issueMapper::toDto)
                .collect(Collectors.toList());
    }

    public IssueResponseTo getById(Long id) {
        Issue issue = issueRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Issue not found with id: " + id));
        return issueMapper.toDto(issue);
    }

    public IssueResponseTo create(IssueRequestTo request) {
        if (!editorRepository.existsById(request.getEditorId())) {
            throw new NotFoundException("Editor not found with id: " + request.getEditorId());
        }
        if (issueRepository.existsByTitle(request.getTitle())) {
            throw new AlreadyExistsException("Issue with title '" + request.getTitle() + "' already exists");
        }
        Issue issue = issueMapper.toEntity(request);
        issue.setId(null);
        issue.setCreated(LocalDateTime.now());
        issue.setModified(LocalDateTime.now());
        if (issue.getMarkerIds() == null) {
            issue.setMarkerIds(new HashSet<>());
        }
        Issue saved = issueRepository.save(issue);
        return issueMapper.toDto(saved);
    }

    public IssueResponseTo update(IssueRequestTo request) {
        if (request.getId() == null) {
            throw new IllegalArgumentException("Issue ID is required for update");
        }
        Issue issue = issueRepository.findById(request.getId())
                .orElseThrow(() -> new NotFoundException("Issue not found with id: " + request.getId()));
        if (!editorRepository.existsById(request.getEditorId())) {
            throw new NotFoundException("Editor not found with id: " + request.getEditorId());
        }
        if (issueRepository.existsByTitleAndIdNot(request.getTitle(), request.getId())) {
            throw new AlreadyExistsException("Issue with title '" + request.getTitle() + "' already exists");
        }
        LocalDateTime created = issue.getCreated();
        issueMapper.updateEntity(issue, request);
        issue.setCreated(created);
        issue.setModified(LocalDateTime.now());
        if (issue.getMarkerIds() == null) {
            issue.setMarkerIds(new HashSet<>());
        }
        Issue updated = issueRepository.save(issue);
        return issueMapper.toDto(updated);
    }

    public void delete(Long id) {
        if (!issueRepository.existsById(id)) {
            throw new NotFoundException("Issue not found with id: " + id);
        }
        issueRepository.deleteById(id);
    }
}
