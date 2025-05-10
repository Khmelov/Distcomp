package com.example.restservice.kafka;

import com.example.restservice.model.Message;
import com.example.restservice.model.MessageEvent;
import com.example.restservice.repository.MessageRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class KafkaMessageConsumer {
    private final MessageRepository messageRepository;
    public KafkaMessageConsumer(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }
    @KafkaListener(topics = "OutTopic", groupId = "discussion-group", containerFactory = "kafkaListenerContainerFactory")
    public void listen(MessageEvent message) {
        try {
            Message existing = messageRepository.findById(message.getId()).orElseThrow(() -> new NoSuchElementException("Message not found"));
            existing.setStatus(message.getStatus());
            messageRepository.save(existing);
        } catch (Exception ex) {

        }
    }
}