package com.sergey.orsik.kafka;

import com.sergey.orsik.dto.CommentState;
import com.sergey.orsik.dto.kafka.CommentTransportOperation;
import com.sergey.orsik.dto.kafka.CommentTransportReply;
import com.sergey.orsik.dto.kafka.CommentTransportRequest;
import com.sergey.orsik.dto.response.CommentResponseTo;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Minimal "discussion" stub: consumes {@code InTopic} and answers synchronous RPCs on {@code OutTopic}.
 */
@TestConfiguration
public class DiscussionStubKafkaConfig {

    @Bean
    ProducerFactory<String, CommentTransportReply> stubReplyProducerFactory(
            @Value("${spring.kafka.bootstrap-servers}") String bootstrapServers) {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        JsonSerializer<CommentTransportReply> ser = new JsonSerializer<>();
        ser.setAddTypeInfo(false);
        return new DefaultKafkaProducerFactory<>(props, new StringSerializer(), ser);
    }

    @Bean
    KafkaTemplate<String, CommentTransportReply> stubReplyKafkaTemplate(
            ProducerFactory<String, CommentTransportReply> stubReplyProducerFactory) {
        return new KafkaTemplate<>(stubReplyProducerFactory);
    }

    @Bean
    ConsumerFactory<String, CommentTransportRequest> stubRequestConsumerFactory(
            @Value("${spring.kafka.bootstrap-servers}") String bootstrapServers) {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "discussion-stub");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        JsonDeserializer<CommentTransportRequest> deser = new JsonDeserializer<>(CommentTransportRequest.class, false);
        deser.addTrustedPackages(
                "com.sergey.orsik.dto.kafka",
                "com.sergey.orsik.dto.request",
                "com.sergey.orsik.dto.response",
                "com.sergey.orsik.dto");
        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deser);
    }

    @Bean
    ConcurrentKafkaListenerContainerFactory<String, CommentTransportRequest> stubInListenerContainerFactory(
            ConsumerFactory<String, CommentTransportRequest> stubRequestConsumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, CommentTransportRequest> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(stubRequestConsumerFactory);
        return factory;
    }

    @Bean
    DiscussionStubListener discussionStubListener(
            KafkaTemplate<String, CommentTransportReply> stubReplyKafkaTemplate,
            @Value("${kafka.topic.out:OutTopic}") String outTopic) {
        return new DiscussionStubListener(stubReplyKafkaTemplate, outTopic);
    }

    static class DiscussionStubListener {

        private final KafkaTemplate<String, CommentTransportReply> replyKafkaTemplate;
        private final String outTopic;

        DiscussionStubListener(KafkaTemplate<String, CommentTransportReply> replyKafkaTemplate, String outTopic) {
            this.replyKafkaTemplate = replyKafkaTemplate;
            this.outTopic = outTopic;
        }

        @KafkaListener(
                topics = "${kafka.topic.in:InTopic}",
                groupId = "discussion-stub",
                containerFactory = "stubInListenerContainerFactory")
        public void onIn(CommentTransportRequest request) {
            if (request == null || request.getOperation() == null) {
                return;
            }
            if (request.getOperation() == CommentTransportOperation.CREATE_ASYNC) {
                return;
            }
            String cid = request.getCorrelationId();
            if (cid == null) {
                return;
            }
            switch (request.getOperation()) {
                case GET_BY_ID -> {
                    Long id = request.getCommentId();
                    if (id != null && id == 404L) {
                        replyKafkaTemplate.send(
                                outTopic,
                                cid,
                                new CommentTransportReply(
                                        cid, true, "Comment with id 404 not found", "Comment", 404L, null, null));
                    } else if (id != null) {
                        replyKafkaTemplate.send(
                                outTopic,
                                cid,
                                new CommentTransportReply(
                                        cid,
                                        false,
                                        null,
                                        null,
                                        null,
                                        new CommentResponseTo(
                                                id, 2L, 1L, "stub-body", Instant.parse("2020-01-01T00:00:00Z"), CommentState.APPROVE),
                                        null));
                    }
                }
                case FIND_ALL -> replyKafkaTemplate.send(
                        outTopic,
                        cid,
                        new CommentTransportReply(
                                cid,
                                false,
                                null,
                                null,
                                null,
                                null,
                                List.of(new CommentResponseTo(
                                        1L, 9L, 1L, "a", Instant.parse("2020-01-01T00:00:00Z"), CommentState.APPROVE))));
                case UPDATE -> {
                    if (request.getBody() != null && request.getCommentId() != null) {
                        replyKafkaTemplate.send(
                                outTopic,
                                cid,
                                new CommentTransportReply(
                                        cid,
                                        false,
                                        null,
                                        null,
                                        null,
                                        new CommentResponseTo(
                                                request.getCommentId(),
                                                request.getBody().getTweetId(),
                                                request.getBody().getCreatorId(),
                                                request.getBody().getContent(),
                                                Instant.now(),
                                                CommentState.APPROVE),
                                        null));
                    }
                }
                case DELETE_BY_ID -> replyKafkaTemplate.send(
                        outTopic, cid, new CommentTransportReply(cid, false, null, null, null, null, null));
                default -> {
                }
            }
        }
    }
}
