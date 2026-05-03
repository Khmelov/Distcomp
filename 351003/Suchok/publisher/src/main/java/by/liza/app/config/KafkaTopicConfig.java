package by.liza.app.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {
    @Value("${kafka.topic.in}") private String inTopic;
    @Value("${kafka.topic.out}") private String outTopic;

    @Bean public NewTopic inTopic() {
        return TopicBuilder.name(inTopic).partitions(3).replicas(1).build();
    }
    @Bean public NewTopic outTopic() {
        return TopicBuilder.name(outTopic).partitions(3).replicas(1).build();
    }
}