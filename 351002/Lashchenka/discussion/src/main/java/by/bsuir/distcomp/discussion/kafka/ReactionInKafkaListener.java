package by.bsuir.distcomp.discussion.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ReactionInKafkaListener {

    private static final Logger log = LoggerFactory.getLogger(ReactionInKafkaListener.class);

    private final ObjectMapper objectMapper;
    private final ReactionInKafkaHandler handler;
    private final ReactionOutKafkaProducer outProducer;

    public ReactionInKafkaListener(
            ObjectMapper objectMapper,
            ReactionInKafkaHandler handler,
            ReactionOutKafkaProducer outProducer) {
        this.objectMapper = objectMapper;
        this.handler = handler;
        this.outProducer = outProducer;
    }

    @KafkaListener(topics = "${kafka.topic.in}", groupId = "${spring.kafka.consumer.group-id}")
    public void onMessage(String payload) {
        try {
            ReactionInMessage msg = objectMapper.readValue(payload, ReactionInMessage.class);
            if (ReactionInKafkaHandler.OP_CREATE.equals(msg.getOperation())) {
                try {
                    handler.handleCreate(msg);
                } catch (Exception e) {
                    log.warn("Kafka CREATE failed: {}", e.getMessage());
                }
                return;
            }
            if (msg.getCorrelationId() == null) {
                return;
            }
            ReactionOutMessage out = handler.handleSync(msg);
            try {
                outProducer.send(out);
            } catch (Exception ex) {
                log.error("Kafka OutTopic send failed", ex);
            }
        } catch (Exception e) {
            log.error("Kafka InTopic processing failed", e);
        }
    }
}
