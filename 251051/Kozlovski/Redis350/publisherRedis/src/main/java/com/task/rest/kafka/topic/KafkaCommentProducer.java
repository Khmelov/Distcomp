package com.task.rest.kafka.topic;

import com.task.rest.kafka.dto.KafkaCommentMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaCommentProducer {

    private static final Logger log = LoggerFactory.getLogger(KafkaCommentProducer.class);

    @Autowired
    private KafkaTemplate<String, KafkaCommentMessage> kafkaTemplate;

    public void sendToInTopic(KafkaCommentMessage message) {
        // Ключ — tweetId (строка), чтобы все комментарии к одному твиту попадали в одну партицию
        String key = String.valueOf(message.getTweetId());
        log.info("Sending to InTopic with key={}, message={}", key, message);
        kafkaTemplate.send("InTopic", key, message);
    }
}