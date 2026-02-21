package com.example.demo.service;

import com.example.demo.dto.requests.MessageRequestTo;
import com.example.demo.dto.responses.MessageResponseTo;
import com.example.demo.model.Message;
import com.example.demo.repository.MessageRepository;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
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
        List<Message> list = repository.findAll();
        return mapper.toMessageResponseList(list);
    }

    public MessageResponseTo findById(Long id) {
        Message msg = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Message not found: " + id));
        return mapper.toMessageResponse(msg);
    }

    public MessageResponseTo update(Long id, MessageRequestTo dto) {
        Message existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Message not found: " + id));

        mapper.updateMessage(dto, existing);
        Message updated = repository.save(existing);
        return mapper.toMessageResponse(updated);
    }
    public void delete(Long id){
        if(!repository.existsById(id)){
            throw new RuntimeException("Message not found: " + id);
        }
        repository.deleteById(id);
    }
}
