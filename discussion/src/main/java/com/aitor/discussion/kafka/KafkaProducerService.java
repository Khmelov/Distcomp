package com.aitor.discussion.kafka;

import com.aitor.publisher.dto.MessageResponseTo;
import lombok.AllArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
@AllArgsConstructor
public class KafkaProducerService {

    private final KafkaTemplate<String, List<MessageResponseTo>> kafkaTemplate;

    public void sendMessage(List<MessageResponseTo> messageList, String key) {
        kafkaTemplate.send("OutTopic", key, messageList);
    }

    public void sendMessage(MessageResponseTo message, String key) {
        kafkaTemplate.send("OutTopic", key, new LinkedList<>(List.of(message)));
    }
}
