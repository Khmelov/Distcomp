package com.distcomp.publisher.impl;

import com.distcomp.config.kafka.KafkaTopic;
import com.distcomp.config.kafka.KafkaTopicProperties;
import com.distcomp.errorhandling.exceptions.kafka.KafkaPublishException;
import com.distcomp.event.abstraction.KafkaEvent;
import com.distcomp.publisher.abstraction.KafkaEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaEventPublisherImpl implements KafkaEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final KafkaTopicProperties kafkaTopicProperties;

    @Override
    public <T extends KafkaEvent> CompletableFuture<Void> publish(final KafkaTopic topic, final T event) {
        return publish(topic, event.getEventId(), event);
    }

    @Override
    public <T extends KafkaEvent> CompletableFuture<Void> publish(final KafkaTopic topic, final String key, final T event) {
        final String topicName = kafkaTopicProperties.getTopicName(topic);
        
        log.debug("Publishing event {} to topic {} with key {}", 
                event.getEventType(), topicName, key);

        return kafkaTemplate.send(topicName, key, event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish event {} to topic {}",
                                event.getEventType(), topicName, ex);
                    } else {
                        log.debug("Successfully published event {} to topic {} partition {} offset {}",
                                event.getEventType(),
                                topicName,
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset());
                    }
                })
                .thenApply(_ -> null);
    }

    @Override
    public <T extends KafkaEvent> void publishSync(final KafkaTopic topic, final T event) {
        try {
            publish(topic, event).get();
        } catch (final Exception e) {
            log.error("Synchronous publish failed for event {}", event.getEventType(), e);
            throw new KafkaPublishException("Failed to publish event: " + event.getEventType(), e);
        }
    }
}