package com.example.demo.service;

import com.example.demo.dto.requests.MessageRequestTo;
import com.example.demo.dto.responses.AuthorResponseTo;
import com.example.demo.dto.responses.MarkResponseTo;
import com.example.demo.dto.responses.MessageResponseTo;
import com.example.demo.exception.DuplicateException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.model.Issue;
import com.example.demo.model.Message;
import com.example.demo.repository.IssueRepository;
import com.example.demo.repository.MessageRepository;
import com.example.demo.specification.MessageSpecifications;
import jakarta.transaction.Transactional;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class MessageService {
    private final MessageRepository repository;
    private final EntityMapper mapper;
    private final IssueRepository issueRepository;

    public MessageService(MessageRepository repository, IssueRepository issueRepository, EntityMapper mapper) {
        this.repository = repository;
        this.issueRepository = issueRepository;
        this.mapper = mapper;
    }

    public MessageResponseTo create(MessageRequestTo dto) {
        // Проверка существования Issue
        Issue issue = issueRepository.findById(dto.getIssueId())
                .orElseThrow(() -> new NotFoundException("Issue not found"));

        // Проверка уникальности content (если требуется)
        if (repository.existsByContent(dto.getContent())) {
            throw new DuplicateException("Content already exists");
        }

        Message entity = mapper.toEntity(dto);
        entity.setIssue(issue); // устанавливаем связь
        Message saved = repository.save(entity);
        return mapper.toMessageResponse(saved);
    }

    public MessageResponseTo update(Long id, MessageRequestTo dto) {
        Message existing = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Message not found"));

        // Проверка уникальности content (исключая текущее)
        if (!existing.getContent().equals(dto.getContent()) &&
                repository.existsByContentAndIdNot(dto.getContent(), id)) {
            throw new DuplicateException("Content already exists");
        }

        // Проверка Issue, если изменился issueId
        if (!existing.getIssue().getId().equals(dto.getIssueId())) {
            Issue newIssue = issueRepository.findById(dto.getIssueId())
                    .orElseThrow(() -> new NotFoundException("Issue not found"));
            existing.setIssue(newIssue);
        }

        mapper.updateMessage(dto, existing); // обновляет content и др.
        Message updated = repository.save(existing);
        return mapper.toMessageResponse(updated);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new NotFoundException("Message not found");
        }
        repository.deleteById(id);
    }

    public List<MessageResponseTo> findAll(String content, Long issueId) {
        Specification<Message> spec = MessageSpecifications.withFilters(content, issueId);
        return repository.findAll(spec).stream()
                .map(mapper::toMessageResponse)
                .collect(Collectors.toList());
    }

    public Page<MessageResponseTo> findAll(Pageable pageable, String content, Long issueId) {
        Specification<Message> spec = MessageSpecifications.withFilters(content, issueId);
        return repository.findAll(spec, pageable)
                .map(mapper::toMessageResponse);
    }

    public MessageResponseTo findById(Long id) {
        Message msg = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Message not found"));
        return mapper.toMessageResponse(msg);
    }
}
