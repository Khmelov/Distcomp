package by.bsuir.distcomp.reaction;

import by.bsuir.distcomp.kafka.ReactionInMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class ReactionInKafkaProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final String inTopic;

    public ReactionInKafkaProducer(
            KafkaTemplate<String, String> kafkaTemplate,
            ObjectMapper objectMapper,
            @Value("${kafka.topic.in}") String inTopic) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
        this.inTopic = inTopic;
    }

    public void send(ReactionInMessage message, String partitionKey) throws JsonProcessingException {
        String json = objectMapper.writeValueAsString(message);
        kafkaTemplate.send(inTopic, partitionKey, json);
    }
}
