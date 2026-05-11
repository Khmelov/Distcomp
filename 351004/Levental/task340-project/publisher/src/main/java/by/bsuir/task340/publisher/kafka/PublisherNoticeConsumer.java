package by.bsuir.task340.publisher.kafka;

import by.bsuir.task340.publisher.dto.NoticeKafkaMessage;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class PublisherNoticeConsumer {

    @KafkaListener(
            topics = "OutTopic",
            groupId = "publisher-group",
            containerFactory = "publisherKafkaListenerContainerFactory"
    )
    public void consume(NoticeKafkaMessage message) {
        System.out.println("Publisher received from OutTopic: " + message);
    }
}