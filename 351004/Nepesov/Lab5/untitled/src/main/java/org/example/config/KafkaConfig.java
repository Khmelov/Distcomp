package org.example.config;

import org.example.dto.PostMessage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;

import java.util.UUID;

@Configuration
public class KafkaConfig {

    @Bean
    public KafkaTemplate<String, PostMessage> kafkaTemplate(ProducerFactory<String, PostMessage> pf) {
        return new KafkaTemplate<>(pf);
    }

    @Bean
    public ReplyingKafkaTemplate<String, PostMessage, PostMessage> replyingKafkaTemplate(
            ProducerFactory<String, PostMessage> pf,
            ConcurrentMessageListenerContainer<String, PostMessage> repliesContainer) {
        return new ReplyingKafkaTemplate<>(pf, repliesContainer);
    }

    @Bean
    public ConcurrentMessageListenerContainer<String, PostMessage> repliesContainer(
            ConcurrentKafkaListenerContainerFactory<String, PostMessage> containerFactory) {
        ConcurrentMessageListenerContainer<String, PostMessage> container =
                containerFactory.createContainer("OutTopic");

        container.getContainerProperties().setGroupId("reply-group-" + UUID.randomUUID());

        return container;
    }
}