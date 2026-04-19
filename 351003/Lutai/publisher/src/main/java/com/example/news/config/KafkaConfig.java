package com.example.news.config; // или .discussion.config

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic inTopic() {
        return TopicBuilder.name("InTopic")
                .partitions(3) // Разделяем данные на 3 части для масштабирования
                .replicas(1)   // Replication Factor (для 1 брокера ставим 1)
                .build();
    }

    @Bean
    public NewTopic outTopic() {
        return TopicBuilder.name("OutTopic")
                .partitions(3)
                .replicas(1)
                .build();
    }
}