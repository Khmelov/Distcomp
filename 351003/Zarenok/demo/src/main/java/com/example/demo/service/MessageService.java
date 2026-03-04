package com.example.demo.service;

import com.example.demo.dto.requests.MessageRequestTo;
import com.example.demo.dto.responses.MarkResponseTo;
import com.example.demo.dto.responses.MessageResponseTo;
import com.example.demo.model.Message;
import com.example.demo.repository.MessageRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class MessageService {
    private final MessageRepository repository;
    private final EntityMapper mapper;

    public MessageService(MessageRepository repository, EntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public MessageResponseTo create(MessageRequestTo dto) {
        Message message = mapper.toEntity(dto);
        Message saved = repository.save(message);
        return mapper.toMessageResponse(saved);
    }

    public List<MessageResponseTo> findAll() {
        return repository.findAll().stream()
                .map(mapper::toMessageResponse)
                .collect(Collectors.toList());
    }

    public MessageResponseTo findById(Long id)
            throws ChangeSetPersister.NotFoundException {
        Message msg = repository.findById(id)
                .orElseThrow(ChangeSetPersister.NotFoundException::new);
        return mapper.toMessageResponse(msg);
    }

    public MessageResponseTo update(Long id, MessageRequestTo dto)
            throws ChangeSetPersister.NotFoundException {
        Message existing = repository.findById(id)
                .orElseThrow(ChangeSetPersister.NotFoundException::new);

        mapper.updateMessage(dto, existing);
        Message updated = repository.save(existing);
        return mapper.toMessageResponse(updated);
    }
    public void delete(Long id) throws ChangeSetPersister.NotFoundException
    {
        if(!repository.existsById(id)){
            throw new ChangeSetPersister.NotFoundException();
        }
        repository.deleteById(id);
    }
}
