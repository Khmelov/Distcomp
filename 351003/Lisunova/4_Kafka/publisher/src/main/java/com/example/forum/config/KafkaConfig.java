package com.example.forum.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;

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

    // 2. Бин для ReplyingKafkaTemplate
    @Bean
    public ReplyingKafkaTemplate<String, Object, Object> replyingKafkaTemplate(
            ProducerFactory<String, Object> pf,
            ConcurrentMessageListenerContainer<String, Object> repliesContainer) {
        return new ReplyingKafkaTemplate<>(pf, repliesContainer);
    }

    // 3. Контейнер для прослушивания ответов из OutTopic
    @Bean
    public ConcurrentMessageListenerContainer<String, Object> repliesContainer(
            ConcurrentKafkaListenerContainerFactory<String, Object> factory) {

        ConcurrentMessageListenerContainer<String, Object> container =
                factory.createContainer("OutTopic");
        container.getContainerProperties().setGroupId("publisher-group");
        return container;
    }
}