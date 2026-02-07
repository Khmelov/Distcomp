package com.example.demo.service;

import com.example.demo.dto.requests.MarkRequestTo;
import com.example.demo.dto.requests.MessageRequestTo;
import com.example.demo.dto.responses.MarkResponseTo;
import com.example.demo.dto.responses.MessageResponseTo;
import com.example.demo.model.Mark;
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

    public MessageResponseTo create(MessageRequestTo dto){
        Message message = mapper.toMessageEntity(dto);
        Message saved = repository.save(message);
        return mapper.toMessageResponse(message);
    }

    public MessageResponseTo findById(Long id){
        Message entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Message not found"));
        return mapper.toMessageResponse(entity);
    }

    public List<MessageResponseTo> findAll(){
        return repository.findAll().stream().map(mapper::toMessageResponse).toList();
    }

    public MessageResponseTo update(Long id, MessageRequestTo dto){
        Message entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Message not found"));
        mapper.updateEntity(dto, entity);
        return mapper.toMessageResponse(repository.save(entity));
    }

    public void delete(Long id){
        if(!repository.existsById(id)){
            throw new RuntimeException("Message not found: " + id);
        }
        repository.deleteById(id);
    }
}
