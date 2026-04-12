package by.bsuir.task340.publisher.kafka;

import by.bsuir.task340.publisher.kafka.dto.ReactionKafkaResponse;
import by.bsuir.task340.publisher.service.ReactionKafkaGateway;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ReactionKafkaReplyListener {
    private final ObjectMapper objectMapper;
    private final ReactionKafkaGateway reactionKafkaGateway;

    public ReactionKafkaReplyListener(ObjectMapper objectMapper, ReactionKafkaGateway reactionKafkaGateway) {
        this.objectMapper = objectMapper;
        this.reactionKafkaGateway = reactionKafkaGateway;
    }

    @KafkaListener(
            topics = "${app.kafka.reaction.out-topic}",
            containerFactory = "publisherKafkaListenerContainerFactory"
    )
    public void listen(String message) throws Exception {
        ReactionKafkaResponse response = objectMapper.readValue(message, ReactionKafkaResponse.class);
        reactionKafkaGateway.complete(response);
    }
}
