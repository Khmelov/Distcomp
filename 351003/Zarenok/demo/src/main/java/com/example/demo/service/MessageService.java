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

    public MessageService(MessageRepository repository) {
        this.repository = repository;
    }

    public MessageResponseTo create(MessageRequestTo dto) {
        Message message = new Message();
        message.setIssueId(dto.getIssueId());
        message.setContent(dto.getContent());

        Message saved = repository.save(message);

        return new MessageResponseTo(
                saved.getId(),
                saved.getIssueId(),
                saved.getContent()
        );
    }

    // Остальные методы
    public List<MessageResponseTo> findAll() {
        return repository.findAll().stream()
                .map(msg -> new MessageResponseTo(
                        msg.getId(),
                        msg.getIssueId(),
                        msg.getContent()
                ))
                .toList();
    }

    public MessageResponseTo findById(Long id) {
        Message msg = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Message not found: " + id));
        return new MessageResponseTo(
                msg.getId(),
                msg.getIssueId(),
                msg.getContent()
        );
    }

    public MessageResponseTo update(Long id, MessageRequestTo dto) {
        Message existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Message not found: " + id));

        existing.setIssueId(dto.getIssueId());
        existing.setContent(dto.getContent());

        Message updated = repository.save(existing);
        return new MessageResponseTo(
                updated.getId(),
                updated.getIssueId(),
                updated.getContent()
        );
    }
    public void delete(Long id){
        if(!repository.existsById(id)){
            throw new RuntimeException("Message not found: " + id);
        }
        repository.deleteById(id);
    }
}
