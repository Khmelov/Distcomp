package com.distcomp.consumer.implementation;

import com.distcomp.config.kafka.KafkaTopic;
import com.distcomp.config.kafka.KafkaTopicProperties;
import com.distcomp.consumer.abstraction.KafkaEventConsumer;
import com.distcomp.event.abstraction.KafkaEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaEventConsumerImpl implements KafkaEventConsumer {

    private final KafkaTopicProperties kafkaTopicProperties;

    @Override
    public <T extends KafkaEvent> void subscribe(final KafkaTopic topic, final Class<T> eventClass, final Consumer<T> handler) {
        subscribe(topic, kafkaTopicProperties
                .getTopics().getDefaultConfig()
                .toString(), eventClass, handler);
    }

    @Override
    public <T extends KafkaEvent> void subscribe(final KafkaTopic topic, final String groupId, final Class<T> eventClass, final Consumer<T> handler) {
        
        
        log.info("Registered consumer for topic {} with group {}", topic.getName(), groupId);
    }
}