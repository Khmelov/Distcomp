package com.homel.user_stories.kafka;

import com.homel.user_stories.dto.NoticeRequestTo;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class NoticeProducer {

    private final KafkaTemplate<String, NoticeRequestTo> kafkaTemplate;

    public NoticeProducer(KafkaTemplate<String, NoticeRequestTo> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendNotice(NoticeRequestTo notice) {
        kafkaTemplate.send("InTopic", notice.getStoryId().toString(), notice);
    }
}
