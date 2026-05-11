package com.github.Lexya06.startrestapp.publisher.impl.config;

import com.github.Lexya06.startrestapp.discussion.api.dto.notice.KafkaNoticeMessage;
import com.github.Lexya06.startrestapp.discussion.api.dto.notice.KafkaNoticeResponseMessage;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;

import java.time.Duration;

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

    @Bean
    public ReplyingKafkaTemplate<Long, KafkaNoticeMessage, KafkaNoticeResponseMessage> replyingKafkaTemplate(
            ProducerFactory<Long, KafkaNoticeMessage> pf,
            ConcurrentMessageListenerContainer<Long, KafkaNoticeResponseMessage> repliesContainer) {
        ReplyingKafkaTemplate<Long, KafkaNoticeMessage, KafkaNoticeResponseMessage> replyTemplate = 
                new ReplyingKafkaTemplate<>(pf, repliesContainer);
        replyTemplate.setDefaultReplyTimeout(Duration.ofSeconds(1));
        return replyTemplate;
    }

    @Bean
    public ConcurrentMessageListenerContainer<Long, KafkaNoticeResponseMessage> repliesContainer(
            ConsumerFactory<Long, KafkaNoticeResponseMessage> cf) {
        ConcurrentMessageListenerContainer<Long, KafkaNoticeResponseMessage> container = 
                new ConcurrentMessageListenerContainer<>(cf, 
                new org.springframework.kafka.listener.ContainerProperties(OUT_TOPIC));
        container.setConcurrency(3);
        return container;
    }
}
