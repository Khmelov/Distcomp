package com.restApp.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    public static final String IN_TOPIC = "InTopic";
    public static final String OUT_TOPIC = "OutTopic";

    @Bean
    public NewTopic inTopic() {
        return TopicBuilder.name(IN_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic outTopic() {
        return TopicBuilder.name(OUT_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
