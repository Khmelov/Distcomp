package by.bsuir.distcomp.discussion.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class ReactionOutKafkaProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final String outTopic;

    public ReactionOutKafkaProducer(
            KafkaTemplate<String, String> kafkaTemplate,
            ObjectMapper objectMapper,
            @Value("${kafka.topic.out}") String outTopic) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
        this.outTopic = outTopic;
    }

    public void send(ReactionOutMessage message) throws JsonProcessingException {
        String json = objectMapper.writeValueAsString(message);
        String key = message.getCorrelationId() != null ? message.getCorrelationId() : "none";
        kafkaTemplate.send(outTopic, key, json);
    }
}
