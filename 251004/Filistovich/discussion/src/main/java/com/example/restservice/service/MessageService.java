package com.example.restservice.service;

import com.example.restservice.model.Message;
import com.example.restservice.model.Status;
import com.example.restservice.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class MessageService {
    private final MessageRepository repository;

    @Autowired
    public MessageService(MessageRepository repository) {
        this.repository = repository;
    }

    public List<Message> findAll() {
        return repository.findAll();
    }

    public Message findById(Long id) {
        return repository.findMessageByCountryAndId("bl", id);
    }

    public Message save(Message message) {
        message.setStatus(Status.APPROVED);
        return repository.save(message);
    }

    public void deleteById(Long id) {
        repository.deleteByCountryAndId("bl", id);
    }

    public Message update(Message message) {
        message.setStatus(Status.APPROVED);
        return repository.save(message);
    }

    @Transactional
    public void updateState(Long messageId, Status status) {
        Message message = repository.findMessageByCountryAndId("bl", messageId);
        message.setStatus(status);
        repository.save(message);
    }

    /*public boolean existsByLogin(String login) {
        return repository.existsByLogin(login);
    }*/
}
