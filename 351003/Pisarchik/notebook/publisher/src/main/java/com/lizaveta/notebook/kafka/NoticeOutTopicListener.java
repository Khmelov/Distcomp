package com.lizaveta.notebook.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lizaveta.notebook.config.KafkaJacksonConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class NoticeOutTopicListener {

    private static final Logger LOG = LoggerFactory.getLogger(NoticeOutTopicListener.class);

    private final ObjectMapper kafkaObjectMapper;
    private final PendingKafkaReplyRegistry pendingReplies;

    public NoticeOutTopicListener(
            @Qualifier(KafkaJacksonConfig.KAFKA_OBJECT_MAPPER) final ObjectMapper kafkaObjectMapper,
            final PendingKafkaReplyRegistry pendingReplies) {
        this.kafkaObjectMapper = kafkaObjectMapper;
        this.pendingReplies = pendingReplies;
    }

    @KafkaListener(
            topics = "${kafka.topic.out-topic}",
            groupId = "${spring.kafka.consumer.group-id}")
    public void onMessage(final String value) {
        try {
            NoticeKafkaOutEnvelope envelope = kafkaObjectMapper.readValue(value, NoticeKafkaOutEnvelope.class);
            if (envelope.correlationId() != null) {
                pendingReplies.complete(envelope.correlationId(), envelope);
            }
        } catch (Exception ex) {
            LOG.error("Failed to process OutTopic message", ex);
        }
    }
}
