//package com.example.rest.kafka;
//
//import org.apache.kafka.clients.admin.NewTopic;
//import org.apache.kafka.common.config.TopicConfig;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.kafka.config.TopicBuilder;
//
//@Configuration
//public class KafkaConfig {
//    @Bean
//    public NewTopic inTopic() {
//        return TopicBuilder.name("InTopic")
//                .partitions(3)  // Устанавливаем 3 партиции
//                .replicas(1)     // Устанавливаем 1 реплику
//                .config(TopicConfig.RETENTION_MS_CONFIG, "86400000") // Параметр для хранения сообщений (например, 1 день)
//                .build();
//    }
//}
