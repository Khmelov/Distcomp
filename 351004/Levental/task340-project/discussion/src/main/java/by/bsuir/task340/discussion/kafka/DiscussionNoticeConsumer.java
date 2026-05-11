package by.bsuir.task340.discussion.kafka;

import by.bsuir.task340.discussion.dto.NoticeKafkaMessage;
import by.bsuir.task340.discussion.service.NoticeModerationService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class DiscussionNoticeConsumer {
    private final DiscussionNoticeProducer producer;
    private final NoticeModerationService moderationService;

    public DiscussionNoticeConsumer(DiscussionNoticeProducer producer, NoticeModerationService moderationService) {
        this.producer = producer;
        this.moderationService = moderationService;
    }

    @KafkaListener(topics = "InTopic", groupId = "discussion-group")
    public void consume(NoticeKafkaMessage message) {
        NoticeKafkaMessage result = moderationService.process(message);
        producer.send(result);
    }
}
