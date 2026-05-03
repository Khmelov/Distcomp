package by.bsuir.task310.config;

import by.bsuir.task310.dto.ReactionRequestTo;
import by.bsuir.task310.dto.ReactionResponseTo;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public ConsumerFactory<String, ReactionRequestTo> reactionRequestConsumerFactory() {
        JsonDeserializer<ReactionRequestTo> deserializer = new JsonDeserializer<>(ReactionRequestTo.class);
        deserializer.addTrustedPackages("*");

        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "discussion-group");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ReactionRequestTo> reactionRequestKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, ReactionRequestTo> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(reactionRequestConsumerFactory());
        return factory;
    }

    @Bean
    public ProducerFactory<String, ReactionResponseTo> reactionResponseProducerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(org.apache.kafka.clients.producer.ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

        return new DefaultKafkaProducerFactory<>(
                props,
                new org.apache.kafka.common.serialization.StringSerializer(),
                new JsonSerializer<>()
        );
    }

    @Bean
    public KafkaTemplate<String, ReactionResponseTo> reactionResponseKafkaTemplate() {
        return new KafkaTemplate<>(reactionResponseProducerFactory());
    }
}