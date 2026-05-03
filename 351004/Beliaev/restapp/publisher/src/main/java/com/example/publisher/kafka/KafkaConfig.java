package com.example.publisher.kafka;

import com.example.publisher.dto.kafka.KafkaNoteRequest;
import com.example.publisher.dto.kafka.KafkaNoteResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;

import java.time.Duration;

@Configuration
public class KafkaConfig {

    @Bean
    public ConcurrentMessageListenerContainer<String, KafkaNoteResponse> repliesContainer(
            ConcurrentKafkaListenerContainerFactory<String, KafkaNoteResponse> containerFactory) {
        ConcurrentMessageListenerContainer<String, KafkaNoteResponse> repliesContainer =
                containerFactory.createContainer("OutTopic");
        repliesContainer.getContainerProperties().setGroupId("publisher-reply-group");
        repliesContainer.setAutoStartup(false);
        return repliesContainer;
    }

    @Bean
    public ReplyingKafkaTemplate<String, KafkaNoteRequest, KafkaNoteResponse> replyingTemplate(
            ProducerFactory<String, KafkaNoteRequest> pf,
            ConcurrentMessageListenerContainer<String, KafkaNoteResponse> repliesContainer) {
        ReplyingKafkaTemplate<String, KafkaNoteRequest, KafkaNoteResponse> template =
                new ReplyingKafkaTemplate<>(pf, repliesContainer);
        // Таймаут в 1 секунду по заданию!
        template.setDefaultReplyTimeout(Duration.ofSeconds(1));
        return template;
    }
}