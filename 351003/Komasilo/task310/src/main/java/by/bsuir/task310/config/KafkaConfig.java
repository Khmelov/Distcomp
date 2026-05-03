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
    public ProducerFactory<String, ReactionRequestTo> reactionRequestProducerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(org.apache.kafka.clients.producer.ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

        return new DefaultKafkaProducerFactory<>(
                props,
                new org.apache.kafka.common.serialization.StringSerializer(),
                new JsonSerializer<>()
        );
    }

    @Bean
    public KafkaTemplate<String, ReactionRequestTo> reactionRequestKafkaTemplate() {
        return new KafkaTemplate<>(reactionRequestProducerFactory());
    }

    @Bean
    public ConsumerFactory<String, ReactionResponseTo> reactionResponseConsumerFactory() {
        JsonDeserializer<ReactionResponseTo> deserializer = new JsonDeserializer<>(ReactionResponseTo.class);
        deserializer.addTrustedPackages("*");

        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "publisher-group");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ReactionResponseTo> reactionResponseKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, ReactionResponseTo> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(reactionResponseConsumerFactory());
        return factory;
    }
}