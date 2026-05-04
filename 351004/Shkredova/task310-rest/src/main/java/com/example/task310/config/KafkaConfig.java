package com.example.task310.config;

import com.example.task310.dto.kafka.NoticeMessage;
import org.apache.kafka.clients.admin.NewTopic;
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
    public NewTopic inTopic() {
        return new NewTopic("InTopic", 3, (short) 1);
    }

    @Bean
    public NewTopic outTopic() {
        return new NewTopic("OutTopic", 3, (short) 1);
    }

    @Bean
    public ReplyingKafkaTemplate<String, NoticeMessage, NoticeMessage> replyingTemplate(
            ProducerFactory<String, NoticeMessage> pf,
            ConcurrentKafkaListenerContainerFactory<String, NoticeMessage> factory) {

        ConcurrentMessageListenerContainer<String, NoticeMessage> replyContainer =
                factory.createContainer("OutTopic");
        replyContainer.getContainerProperties().setMissingTopicsFatal(false);
        replyContainer.getContainerProperties().setGroupId("publisher-reply-group");

        ReplyingKafkaTemplate<String, NoticeMessage, NoticeMessage> template =
                new ReplyingKafkaTemplate<>(pf, replyContainer);
        template.setDefaultReplyTimeout(Duration.ofSeconds(5));
        return template;
    }
}