package com.example.app.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {
    
    @Bean
    public NewTopic inTopic() {
        return TopicBuilder.name("in-topic")
                .partitions(3)
                .replicas(1)
                .build();
    }
    
    @Bean
    public NewTopic outTopic() {
        return TopicBuilder.name("out-topic")
                .partitions(3)
                .replicas(1)
                .build();
    }
}