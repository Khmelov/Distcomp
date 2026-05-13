package com.bsuir.distcomp.kafka;

import com.bsuir.distcomp.dto.CommentRequestTo;
import com.bsuir.distcomp.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentResponseConsumer {

    private final CommentService service;

    @KafkaListener(topics = "InTopic", groupId = "discussion-group")
    public void listen(@Payload CommentRequestTo dto) {
        log.info("🟠 [STEP 3] Discussion received from InTopic: operation={}, correlationId={}, content={}",
                dto.getOperation(),
                dto.getCorrelationId(),
                dto.getContent());

        try {
            service.process(dto);
            log.info("✅ [STEP 3] Discussion processed successfully");
        } catch (Exception e) {
            log.error("🔴 [STEP 3] Error processing in Discussion: {}", e.getMessage(), e);
        }
    }
}