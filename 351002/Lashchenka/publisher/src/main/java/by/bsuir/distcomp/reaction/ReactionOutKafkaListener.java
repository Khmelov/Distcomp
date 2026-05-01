package by.bsuir.distcomp.reaction;

import by.bsuir.distcomp.kafka.ReactionOutMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ReactionOutKafkaListener {

    private final ObjectMapper objectMapper;
    private final ReactionReplyRegistry registry;

    public ReactionOutKafkaListener(ObjectMapper objectMapper, ReactionReplyRegistry registry) {
        this.objectMapper = objectMapper;
        this.registry = registry;
    }

    @KafkaListener(topics = "${kafka.topic.out}", groupId = "${spring.kafka.consumer.group-id}")
    public void onMessage(String payload) {
        try {
            ReactionOutMessage msg = objectMapper.readValue(payload, ReactionOutMessage.class);
            if (msg.getCorrelationId() != null) {
                registry.complete(msg.getCorrelationId(), msg);
            }
        } catch (Exception ignored) {
        }
    }
}
