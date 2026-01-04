package com.blog.service;

import com.blog.dto.response.MessageResponseTo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaResponseConsumer {
/*
    private static final Logger logger = LoggerFactory.getLogger(KafkaResponseConsumer.class);

    @KafkaListener(topics = "${kafka.topic.out.name}", groupId = "publisher-group")
    public void consumeResponse(MessageResponseTo response) {
        logger.info("Received response from discussion module:");
        logger.info("Message ID: {}", response.getId());
        logger.info("Content: {}", response.getContent());
        logger.info("State: {}", response.getState());
        logger.info("Topic ID: {}", response.getTopicId());

        // Здесь можно обновить локальную базу данных или кэш
        // с результатом модерации

        if ("APPROVE".equals(response.getState())) {
            logger.info("Message approved and stored in discussion module");
        } else if ("DECLINE".equals(response.getState())) {
            logger.warn("Message declined by moderator");
        }
    }*/
}