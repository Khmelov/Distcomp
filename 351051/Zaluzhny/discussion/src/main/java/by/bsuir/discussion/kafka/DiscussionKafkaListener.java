package by.bsuir.discussion.kafka;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "app.kafka.enabled", havingValue = "true", matchIfMissing = true)
public class DiscussionKafkaListener {

    private final DiscussionRequestHandler handler;

    public DiscussionKafkaListener(DiscussionRequestHandler handler) {
        this.handler = handler;
    }

    @KafkaListener(topics = "${discussion.topics.in}", groupId = "discussion")
    public void onRequest(MessageEvent request) {
        handler.handle(request);
    }
}
