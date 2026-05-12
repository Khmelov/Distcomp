package com.bsuir.distcomp.kafka;

import com.bsuir.distcomp.dto.CommentRequestTo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentProducer {

    private final KafkaTemplate<String, CommentRequestTo> kafkaTemplate;

    public void send(CommentRequestTo dto) {
        String key = dto.getTopicId() == null ? "default" : dto.getTopicId().toString();

        log.info("🟡 [STEP 2] Sending to InTopic: key={}, operation={}, correlationId={}",
                key, dto.getOperation(), dto.getCorrelationId());

        CompletableFuture<SendResult<String, CommentRequestTo>> future =
                kafkaTemplate.send("InTopic", key, dto);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("✅ [STEP 2] Successfully sent to InTopic: offset={}, partition={}",
                        result.getRecordMetadata().offset(),
                        result.getRecordMetadata().partition());
            } else {
                log.error("🔴 [STEP 2] Failed to send to InTopic: {}", ex.getMessage(), ex);
            }
        });
    }
}