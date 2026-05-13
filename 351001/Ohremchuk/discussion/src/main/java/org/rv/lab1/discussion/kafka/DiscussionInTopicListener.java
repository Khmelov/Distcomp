package org.rv.lab1.discussion.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.rv.lab1.discussion.service.CommentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Component
public class DiscussionInTopicListener {
    private static final Logger log = LoggerFactory.getLogger(DiscussionInTopicListener.class);

    private final ObjectMapper objectMapper;
    private final CommentService commentService;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${app.kafka.out-topic}")
    private String outTopic;

    public DiscussionInTopicListener(
            ObjectMapper objectMapper,
            CommentService commentService,
            KafkaTemplate<String, String> kafkaTemplate
    ) {
        this.objectMapper = objectMapper;
        this.commentService = commentService;
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = "${app.kafka.in-topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void onInTopic(String payload) {
        CommentCommandMessage cmd = null;
        try {
            cmd = objectMapper.readValue(payload, CommentCommandMessage.class);
        } catch (JsonProcessingException e) {
            log.warn("Invalid InTopic payload: {}", e.getMessage());
            try {
                JsonNode node = objectMapper.readTree(payload);
                if (node.hasNonNull("requestId")) {
                    UUID rid = UUID.fromString(node.get("requestId").asText());
                    sendReply(CommentReplyMessage.fail(rid, "Invalid command payload"));
                }
            } catch (Exception ex) {
                log.debug("Could not build fail reply for invalid payload", ex);
            }
            return;
        }
        CommentReplyMessage reply = commentService.processKafkaCommand(cmd);
        sendReply(reply);
    }

    /**
     * Wait for broker ack so the publisher does not block until HTTP/client timeout on missing OutTopic reply.
     */
    private void sendReply(CommentReplyMessage reply) {
        if (reply == null || reply.requestId() == null) {
            return;
        }
        try {
            String out = objectMapper.writeValueAsString(reply);
            String key = reply.requestId().toString();
            kafkaTemplate.send(outTopic, key, out).get(15, TimeUnit.SECONDS);
        } catch (JsonProcessingException e) {
            log.error("Cannot serialize reply for requestId={}", reply.requestId(), e);
        } catch (ExecutionException | TimeoutException e) {
            log.error("OutTopic send failed for requestId={}", reply.requestId(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("OutTopic send interrupted for requestId={}", reply.requestId(), e);
        }
    }
}
