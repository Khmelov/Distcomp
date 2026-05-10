package com.adashkevich.kafka.lab.config;

import com.adashkevich.kafka.lab.kafka.KafkaReplyMessage;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
@EnableConfigurationProperties(KafkaTopicProperties.class)
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public NewTopic inTopic(KafkaTopicProperties properties) {
        return TopicBuilder.name(properties.getInTopic()).partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic outTopic(KafkaTopicProperties properties) {
        return TopicBuilder.name(properties.getOutTopic()).partitions(3).replicas(1).build();
    }

    @Bean
    public ConsumerFactory<String, KafkaReplyMessage> kafkaReplyConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "publisher-group");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);

        JsonDeserializer<KafkaReplyMessage> deserializer =
                new JsonDeserializer<>(KafkaReplyMessage.class, false);
        deserializer.addTrustedPackages("*");

        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                deserializer
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, KafkaReplyMessage> kafkaListenerContainerFactory(
            ConsumerFactory<String, KafkaReplyMessage> kafkaReplyConsumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, KafkaReplyMessage> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(kafkaReplyConsumerFactory);
        return factory;
    }
}