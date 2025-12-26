package com.restApp.service;

import com.restApp.dto.CommentResponseTo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {

    private static final Logger log = LoggerFactory.getLogger(KafkaConsumerService.class);

    @KafkaListener(topics = "OutTopic", groupId = "publisher-group")
    public void consume(CommentResponseTo response) {
        log.info("Received comment response from OutTopic: {}", response);
        // Additional logic like updating a cache or notifying a user could go here
    }
}
