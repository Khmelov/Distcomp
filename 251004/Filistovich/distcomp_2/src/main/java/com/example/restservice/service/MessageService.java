package com.example.restservice.service;

import com.example.restservice.kafka.KafkaMessageProducer;
import com.example.restservice.model.Message;
import com.example.restservice.model.MessageEvent;
import com.example.restservice.model.Status;
import com.example.restservice.repository.MessageRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository repository;
    private final KafkaMessageProducer kafkaProducer;

    /*@Autowired
    public MessageService(MessageRepository repository, KafkaMessageProducer kafkaProducer) {
        this.repository = repository;
        this.kafkaProducer = kafkaProducer;
    }*/

    public List<Message> findAll() {
        return repository.findAll();
    }

    public Message findById(Long id) {
        return repository.getMessageById(id);
    }

    public Message save(Message saved) {
        saved.setStatus(Status.PENDING);
        saved = repository.save(saved);


        kafkaProducer.sendMessage(new MessageEvent(
                saved.getId(),
                saved.getNews().getId(),
                saved.getContent(),
                saved.getStatus(),
                LocalDateTime.now(),
                "create"
        ));
        return saved;
    }

    @Transactional
    public void deleteById(Long id) {
        repository.deleteMessageById(id);
        kafkaProducer.sendMessage(new MessageEvent(
                id, 0L, "",
                Status.PENDING,
                LocalDateTime.now(),
                "delete"
        ));
    }

    public Message update(Message message) {
        message.setStatus(Status.PENDING);
        repository.save(message);


        kafkaProducer.sendMessage(new MessageEvent(
                message.getId(),
                message.getNews().getId(),
                message.getContent(),
                message.getStatus(),
                LocalDateTime.now(),
                "update"
        ));
        return message;
    }

    /*public boolean existsByLogin(String login) {
        return repository.existsByLogin(login);
    }*/
}
