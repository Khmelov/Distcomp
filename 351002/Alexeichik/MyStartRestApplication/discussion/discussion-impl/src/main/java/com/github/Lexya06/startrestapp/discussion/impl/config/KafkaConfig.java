package com.github.Lexya06.startrestapp.discussion.impl.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;

@Configuration
@EnableKafka
public class KafkaConfig {
    public static final String IN_TOPIC = "InTopic";
    public static final String OUT_TOPIC = "OutTopic";
}
