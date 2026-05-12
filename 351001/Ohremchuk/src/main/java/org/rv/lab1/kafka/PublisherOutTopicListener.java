package org.rv.lab1.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class PublisherOutTopicListener {
    private static final Logger log = LoggerFactory.getLogger(PublisherOutTopicListener.class);

    private final ObjectMapper objectMapper;
    private final CommentKafkaGateway gateway;

    public PublisherOutTopicListener(ObjectMapper objectMapper, CommentKafkaGateway gateway) {
        this.objectMapper = objectMapper;
        this.gateway = gateway;
    }

    @KafkaListener(topics = "${app.kafka.out-topic}", groupId = "${spring.kafka.consumer.out-group-id}")
    public void onOutTopic(String payload) {
        try {
            CommentReplyMessage reply = objectMapper.readValue(payload, CommentReplyMessage.class);
            gateway.complete(reply);
        } catch (JsonProcessingException e) {
            log.warn("Invalid OutTopic payload: {}", e.getMessage());
        }
    }
}
