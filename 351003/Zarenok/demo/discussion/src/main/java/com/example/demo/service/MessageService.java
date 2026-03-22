package com.example.demo.service;

import com.example.demo.exception.NotFoundException;
import com.example.demo.repository.*;
import com.example.demo.dto.requests.MessageRequestTo;
import com.example.demo.dto.responses.MessageResponseTo;
import com.example.demo.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository repository;
    private final MessageMapper mapper;

    // CREATE
    public MessageResponseTo create(MessageRequestTo dto) {
        Message entity = mapper.toEntity(dto);
        Message saved = repository.save(entity);
        return mapper.toResponse(saved);
    }

    // READ by issueId + id
    public MessageResponseTo findById(Long issueId, Long id) {
        MessageKey key = new MessageKey(issueId, id);
        Message message = repository.findById(key)
                .orElseThrow(() -> new NotFoundException("Message not found with issueId=" + issueId + ", id=" + id));
        return mapper.toResponse(message);
    }

    // READ all messages for a specific issue
    public List<MessageResponseTo> findAllByIssueId(Long issueId) {
        return repository.findByKeyIssueId(issueId).stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    // READ all messages (for debugging, maybe not needed, but added for completeness)
    public List<MessageResponseTo> findAll() {
        return repository.findAll().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    // UPDATE
    public MessageResponseTo update(Long issueId, Long id, MessageRequestTo dto) {
        MessageKey key = new MessageKey(issueId, id);
        Message existing = repository.findById(key)
                .orElseThrow(() -> new NotFoundException("Message not found with issueId=" + issueId + ", id=" + id));

        mapper.updateEntity(dto, existing);
        existing.setKey(key);
        Message updated = repository.save(existing);
        return mapper.toResponse(updated);
    }

    // DELETE
    public void delete(Long issueId, Long id) {
        MessageKey key = new MessageKey(issueId, id);
        if (!repository.existsById(key)) {
            throw new NotFoundException("Message not found with issueId=" + issueId + ", id=" + id);
        }
        repository.deleteById(key);
    }
}
