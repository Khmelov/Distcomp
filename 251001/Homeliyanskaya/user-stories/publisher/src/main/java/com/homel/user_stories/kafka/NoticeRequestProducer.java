package com.homel.user_stories.kafka;

import com.homel.user_stories.dto.NoticeMethodRequestTo;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class NoticeRequestProducer {
    private final KafkaTemplate<String, NoticeMethodRequestTo> kafkaTemplate;

    public NoticeRequestProducer(KafkaTemplate<String, NoticeMethodRequestTo> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendRequest(NoticeMethodRequestTo request) {
        kafkaTemplate.send("InTopic", request.getStoryId().toString(), request);
    }
}
