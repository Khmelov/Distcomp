package com.publick.service;

import com.publick.dto.IssueRequestTo;
import com.publick.dto.IssueResponseTo;
import com.publick.entity.Author;
import com.publick.entity.Issue;
import com.publick.exception.ForbiddenException;
import com.publick.repository.AuthorRepository;
import com.publick.repository.IssueRepository;
import com.publick.service.mapper.IssueMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class IssueService {

    @Autowired
    private IssueRepository issueRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private IssueMapper issueMapper;

    public IssueResponseTo create(IssueRequestTo request) {
        // Проверяем, что автор существует
        Author author = authorRepository.findById(request.getAuthorId())
                .orElseThrow(() -> new ForbiddenException("Author not found with id: " + request.getAuthorId()));

        // Проверяем, что title уникальный
        if (issueRepository.findByTitleIgnoreCase(request.getTitle()).isPresent()) {
            throw new ForbiddenException("Issue with this title already exists");
        }

        Issue issue = issueMapper.toEntity(request, author);
        Issue saved = issueRepository.save(issue);
        return issueMapper.toResponse(saved);
    }

    public IssueResponseTo getById(Long id) {
        Issue issue = issueRepository.findById(id)
                .orElseThrow(() -> new com.publick.exception.NotFoundException("Issue not found with id: " + id));
        return issueMapper.toResponse(issue);
    }

    public List<IssueResponseTo> getAll() {
        return issueRepository.findAll().stream()
                .map(issueMapper::toResponse)
                .collect(Collectors.toList());
    }

    public Page<IssueResponseTo> getAll(Pageable pageable) {
        return issueRepository.findAll(pageable)
                .map(issueMapper::toResponse);
    }

    public IssueResponseTo update(Long id, IssueRequestTo request) {
        Issue existing = issueRepository.findById(id)
                .orElseThrow(() -> new com.publick.exception.NotFoundException("Issue not found with id: " + id));

        // Validate that author exists
        Author author = authorRepository.findById(request.getAuthorId())
                .orElseThrow(() -> new com.publick.exception.NotFoundException("Author not found with id: " + request.getAuthorId()));

        issueMapper.updateEntityFromDto(request, existing, author);
        Issue saved = issueRepository.save(existing);
        return issueMapper.toResponse(saved);
    }

    public void delete(Long id) {
        if (!issueRepository.existsById(id)) {
            throw new com.publick.exception.NotFoundException("Issue not found with id: " + id);
        }
        issueRepository.deleteById(id);
    }
}