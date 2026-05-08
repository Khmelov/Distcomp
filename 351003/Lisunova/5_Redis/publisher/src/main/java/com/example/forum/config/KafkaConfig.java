package com.example.forum.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;

import java.util.UUID;

@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic inTopic() {
        return TopicBuilder.name("InTopic")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic outTopic() {
        return TopicBuilder.name("OutTopic")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public ReplyingKafkaTemplate<String, Object, Object> replyingKafkaTemplate(
            ProducerFactory<String, Object> pf,
            ConcurrentMessageListenerContainer<String, Object> repliesContainer) {

        ReplyingKafkaTemplate<String, Object, Object> template = new ReplyingKafkaTemplate<>(pf, repliesContainer);
        template.setDefaultReplyTimeout(java.time.Duration.ofSeconds(15));
        return template;
    }

    @Bean
    public ConcurrentMessageListenerContainer<String, Object> repliesContainer(
            ConcurrentKafkaListenerContainerFactory<String, Object> factory) {

        ConcurrentMessageListenerContainer<String, Object> container =
                factory.createContainer("OutTopic");
        container.getContainerProperties().setGroupId("replies-group-" + UUID.randomUUID());
        container.setAutoStartup(true);
        return container;
    }
}