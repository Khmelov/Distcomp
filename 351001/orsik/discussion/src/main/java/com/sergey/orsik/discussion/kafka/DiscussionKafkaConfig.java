package com.sergey.orsik.discussion.kafka;

import com.sergey.orsik.dto.kafka.CommentTransportReply;
import com.sergey.orsik.dto.kafka.CommentTransportRequest;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class DiscussionKafkaConfig {

    @Bean
    ProducerFactory<String, CommentTransportReply> commentReplyProducerFactory(
            @Value("${spring.kafka.bootstrap-servers:localhost:9092}") String bootstrapServers) {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        JsonSerializer<CommentTransportReply> valueSerializer = new JsonSerializer<>();
        valueSerializer.setAddTypeInfo(false);
        return new DefaultKafkaProducerFactory<>(props, new StringSerializer(), valueSerializer);
    }

    @Bean
    KafkaTemplate<String, CommentTransportReply> commentReplyKafkaTemplate(
            ProducerFactory<String, CommentTransportReply> commentReplyProducerFactory) {
        return new KafkaTemplate<>(commentReplyProducerFactory);
    }

    @Bean
    ConsumerFactory<String, CommentTransportRequest> commentRequestConsumerFactory(
            @Value("${spring.kafka.bootstrap-servers:localhost:9092}") String bootstrapServers,
            @Value("${spring.kafka.consumer.group-id:discussion-comments}") String groupId) {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        JsonDeserializer<CommentTransportRequest> valueDeserializer =
                new JsonDeserializer<>(CommentTransportRequest.class, false);
        valueDeserializer.addTrustedPackages(
                "com.sergey.orsik.dto.kafka",
                "com.sergey.orsik.dto.request",
                "com.sergey.orsik.dto.response",
                "com.sergey.orsik.dto");
        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), valueDeserializer);
    }

    @Bean
    ConcurrentKafkaListenerContainerFactory<String, CommentTransportRequest> commentRequestKafkaListenerContainerFactory(
            ConsumerFactory<String, CommentTransportRequest> commentRequestConsumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, CommentTransportRequest> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(commentRequestConsumerFactory);
        return factory;
    }
}
