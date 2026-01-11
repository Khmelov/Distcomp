// src/main/java/by/rest/discussion/config/KafkaConfig.java
package by.rest.discussion.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
@EnableKafka
public class KafkaConfig {
    
    @Value("${kafka.topic.in:InTopic}")
    private String commentInTopic;
    
    @Value("${kafka.topic.out:OutTopic}")
    private String commentOutTopic;
    
    @Bean
    public NewTopic commentInTopic() {
        return TopicBuilder.name(commentInTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }
    
    @Bean
    public NewTopic commentOutTopic() {
        return TopicBuilder.name(commentOutTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }
}