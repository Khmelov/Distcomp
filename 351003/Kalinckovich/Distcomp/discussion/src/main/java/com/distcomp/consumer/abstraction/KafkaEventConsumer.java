package com.distcomp.consumer.abstraction;


import com.distcomp.config.kafka.KafkaTopic;
import com.distcomp.event.abstraction.KafkaEvent;

import java.util.function.Consumer;

public interface KafkaEventConsumer {

    <T extends KafkaEvent> void subscribe(KafkaTopic topic, Class<T> eventClass, Consumer<T> handler);

    <T extends KafkaEvent> void subscribe(KafkaTopic topic, String groupId, Class<T> eventClass, Consumer<T> handler);
}