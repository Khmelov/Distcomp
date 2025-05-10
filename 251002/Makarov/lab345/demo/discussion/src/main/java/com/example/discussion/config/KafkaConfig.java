package com.example.discussion.config;

import com.example.discussion.dto.NoteRequestTo;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConfig {

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, NoteRequestTo> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, NoteRequestTo> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        // Настройка десериализатора
        JsonDeserializer<NoteRequestTo> deserializer = new JsonDeserializer<>(NoteRequestTo.class);
        deserializer.setRemoveTypeHeaders(false);
        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeMapperForKey(true);

        // Оборачиваем десериализатор в ErrorHandlingDeserializer для обработки ошибок
        ErrorHandlingDeserializer<NoteRequestTo> errorHandlingDeserializer = new ErrorHandlingDeserializer<>(deserializer);

        // Настройка фабрики потребителей
        Map<String, Object> consumerProps = new HashMap<>();
        consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, "discussion-group");

        ConsumerFactory<String, NoteRequestTo> consumerFactory = new DefaultKafkaConsumerFactory<>(
                consumerProps, new StringDeserializer(), errorHandlingDeserializer
        );

        // Настройка KafkaTemplate с ProducerFactory
        Map<String, Object> producerProps = new HashMap<>();
        producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        ProducerFactory<String, Object> producerFactory = new DefaultKafkaProducerFactory<>(producerProps);
        KafkaTemplate<String, Object> kafkaTemplate = new KafkaTemplate<>(producerFactory);

        // Настройка listener factory
        factory.setConsumerFactory(consumerFactory);
        factory.setReplyTemplate(kafkaTemplate); // Теперь здесь используется KafkaTemplate с ProducerFactory
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        factory.setBatchListener(false);

        return factory;
    }
}
