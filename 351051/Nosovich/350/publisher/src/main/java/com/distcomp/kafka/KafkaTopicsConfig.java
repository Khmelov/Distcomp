package com.distcomp.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/**
 * Declares the topics used by the publisher-to-discussion exchange.
 */
@Configuration
public class KafkaTopicsConfig {

    @Bean
    public NewTopic inTopic(@Value("${app.kafka.in-topic}") String name) {
        return TopicBuilder.name(name).partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic outTopic(@Value("${app.kafka.out-topic}") String name) {
        return TopicBuilder.name(name).partitions(3).replicas(1).build();
    }
}
