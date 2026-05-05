package com.example.forum.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.ConsumerFactory;

@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic inTopic() {
        return TopicBuilder.name("InTopic").partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic outTopic() {
        return TopicBuilder.name("OutTopic").partitions(3).replicas(1).build();
    }

}