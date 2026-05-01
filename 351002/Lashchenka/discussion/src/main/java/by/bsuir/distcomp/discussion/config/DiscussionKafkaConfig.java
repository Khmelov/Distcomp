package by.bsuir.distcomp.discussion.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class DiscussionKafkaConfig {

    @Bean
    public NewTopic inTopic(@Value("${kafka.topic.in}") String name) {
        return TopicBuilder.name(name).partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic outTopic(@Value("${kafka.topic.out}") String name) {
        return TopicBuilder.name(name).partitions(3).replicas(1).build();
    }
}
