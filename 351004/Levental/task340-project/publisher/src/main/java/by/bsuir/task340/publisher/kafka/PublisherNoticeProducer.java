package by.bsuir.task340.publisher.kafka;

import by.bsuir.task340.publisher.dto.NoticeKafkaMessage;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class PublisherNoticeProducer {
    private final KafkaTemplate<String, NoticeKafkaMessage> kafkaTemplate;

    public PublisherNoticeProducer(KafkaTemplate<String, NoticeKafkaMessage> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(NoticeKafkaMessage message) {
        kafkaTemplate.send("InTopic", String.valueOf(message.getArticleId()), message);
    }
}
