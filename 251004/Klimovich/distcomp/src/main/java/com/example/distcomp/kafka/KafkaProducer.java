package com.example.distcomp.kafka;

import com.example.distcomp.dto.MessageRequestKafka;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaProducer {
    private final KafkaTemplate<String, MessageRequestKafka> kafkaTemplate;

    public void sendToKafka(MessageRequestKafka message) {
        kafkaTemplate.send("InTopic", message);
    }
}
