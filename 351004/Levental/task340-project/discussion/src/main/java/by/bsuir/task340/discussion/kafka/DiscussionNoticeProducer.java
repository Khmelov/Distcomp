package by.bsuir.task340.discussion.kafka;

import by.bsuir.task340.discussion.dto.NoticeKafkaMessage;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class DiscussionNoticeProducer {
    private final KafkaTemplate<String, NoticeKafkaMessage> kafkaTemplate;

    public DiscussionNoticeProducer(KafkaTemplate<String, NoticeKafkaMessage> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(NoticeKafkaMessage message) {
        kafkaTemplate.send("OutTopic", String.valueOf(message.getArticleId()), message);
    }
}
