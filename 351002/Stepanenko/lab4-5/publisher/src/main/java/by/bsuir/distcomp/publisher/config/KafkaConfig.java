package by.bsuir.distcomp.publisher.config;

import by.bsuir.distcomp.dto.reaction.ReactionKafkaRequest;
import by.bsuir.distcomp.dto.reaction.ReactionKafkaResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import java.time.Duration;

@Configuration
public class KafkaConfig {
    @Value("${app.kafka.out-topic}")
    private String replyTopic;

    @Bean
    public ReplyingKafkaTemplate<String, ReactionKafkaRequest, ReactionKafkaResponse> replyingKafkaTemplate(
            ProducerFactory<String, ReactionKafkaRequest> pf,
            ConcurrentMessageListenerContainer<String, ReactionKafkaResponse> repliesContainer) {
        ReplyingKafkaTemplate<String, ReactionKafkaRequest, ReactionKafkaResponse> template =
                new ReplyingKafkaTemplate<>(pf, repliesContainer);

        // УВЕЛИЧИВАЕМ таймаут до 10 секунд. Cassandra в Docker может "просыпаться" долго.
        template.setDefaultReplyTimeout(Duration.ofSeconds(10));
        return template;
    }

    @Bean
    public ConcurrentMessageListenerContainer<String, ReactionKafkaResponse> repliesContainer(
            ConcurrentKafkaListenerContainerFactory<String, ReactionKafkaResponse> containerFactory) {
        return containerFactory.createContainer(replyTopic);
    }
}