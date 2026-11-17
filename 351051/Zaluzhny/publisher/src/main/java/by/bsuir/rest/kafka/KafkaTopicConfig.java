package by.bsuir.rest.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
@ConditionalOnProperty(name = "discussion.client", havingValue = "kafka")
public class KafkaTopicConfig {

    @Bean
    public NewTopic publisherInTopic(@Value("${discussion.topics.in}") String name) {
        return TopicBuilder.name(name).partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic publisherOutTopic(@Value("${discussion.topics.out}") String name) {
        return TopicBuilder.name(name).partitions(3).replicas(1).build();
    }
}
