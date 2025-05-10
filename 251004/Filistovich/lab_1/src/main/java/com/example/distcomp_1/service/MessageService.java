package com.example.distcomp_1.service;

import com.example.distcomp_1.mdoel.Marker;
import com.example.distcomp_1.mdoel.Message;
import com.example.distcomp_1.repository.MarkerRepository;
import com.example.distcomp_1.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageService {
    private final MessageRepository repository;

    @Autowired
    public MessageService(MessageRepository repository) {
        this.repository = repository;
    }

    public List<Message> findAll() {
        return repository.getMessages();
    }

    public Message findById(Long id) {
        return repository.get(id);
    }

    public Message save(Message message) {
        return repository.add(message);
    }

    public void deleteById(Long id) {
        repository.delete(id);
    }

    public Message update(Message message) {
        return repository.update(message);
    }

    /*public boolean existsByLogin(String login) {
        return repository.existsByLogin(login);
    }*/
}
