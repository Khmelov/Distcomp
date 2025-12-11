package org.example.kafka;

import org.example.kafka.dto.KafkaCommentMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class DiscussionKafkaProducer {

    private static final Logger log = LoggerFactory.getLogger(DiscussionKafkaProducer.class);

    @Autowired
    private KafkaTemplate<String, KafkaCommentMessage> kafkaTemplate;

    public void sendToOutTopic(KafkaCommentMessage message) {
        String key = String.valueOf(message.getId());
        log.info("Sending to OutTopic: {}", message);
        kafkaTemplate.send("OutTopic", key, message);
    }
}