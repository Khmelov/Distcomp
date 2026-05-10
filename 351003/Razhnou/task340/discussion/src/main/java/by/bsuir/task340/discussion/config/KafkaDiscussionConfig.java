package by.bsuir.task340.discussion.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaDiscussionConfig {

    @Bean
    public NewTopic discussionInTopic(@Value("${app.kafka.reaction.in-topic}") String inTopic) {
        return TopicBuilder.name(inTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic discussionOutTopic(@Value("${app.kafka.reaction.out-topic}") String outTopic) {
        return TopicBuilder.name(outTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public ProducerFactory<String, String> discussionProducerFactory(KafkaProperties kafkaProperties) {
        Map<String, Object> properties = new HashMap<>(kafkaProperties.buildProducerProperties(null));
        return new DefaultKafkaProducerFactory<>(properties);
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate(ProducerFactory<String, String> discussionProducerFactory) {
        return new KafkaTemplate<>(discussionProducerFactory);
    }

    @Bean
    public ConsumerFactory<String, String> discussionConsumerFactory(KafkaProperties kafkaProperties) {
        Map<String, Object> properties = new HashMap<>(kafkaProperties.buildConsumerProperties(null));
        properties.remove(JsonDeserializer.VALUE_DEFAULT_TYPE);
        return new DefaultKafkaConsumerFactory<>(properties);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> discussionKafkaListenerContainerFactory(
            ConsumerFactory<String, String> discussionConsumerFactory
    ) {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(discussionConsumerFactory);
        return factory;
    }
}
