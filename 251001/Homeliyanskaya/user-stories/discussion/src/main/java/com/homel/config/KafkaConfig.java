package com.homel.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;

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
}