package com.example.publisher.kafka;

import com.example.publisher.dto.NoteMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class NoteProducer {

    @Autowired
    private KafkaTemplate<String, NoteMessage> kafkaTemplate;

    public void sendToInTopic(NoteMessage message) {
        String key = String.valueOf(message.getTweetId());
        kafkaTemplate.send("InTopic", key, message);
        System.out.println("Sent to InTopic: Note " + message.getNoteId());
    }
}