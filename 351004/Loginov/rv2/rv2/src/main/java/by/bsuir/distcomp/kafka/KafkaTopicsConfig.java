package by.bsuir.distcomp.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicsConfig {
    @Bean
    public NewTopic inTopic(@Value("${kafka.topics.in}") String topic) {
        return TopicBuilder.name(topic).partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic outTopic(@Value("${kafka.topics.out}") String topic) {
        return TopicBuilder.name(topic).partitions(3).replicas(1).build();
    }
}
