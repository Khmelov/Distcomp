package com.bsuir.distcomp.kafka;

import com.bsuir.distcomp.dto.CommentListResponseTo;
import com.bsuir.distcomp.dto.CommentResponseTo;
import com.bsuir.distcomp.dto.KafkaResponse;
import com.bsuir.distcomp.service.ResponseHolder;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class CommentConsumer {

    @Autowired
    private ResponseHolder holder;

    @Autowired
    private ObjectMapper objectMapper;

    @KafkaListener(topics = "OutTopic", groupId = "publisher-group")
    public void listen(@Payload KafkaResponse response) {

        log.info("🔵 Received KafkaResponse: type={}, correlationId={}",
                response.getType(), response.getCorrelationId());

        if (response.getCorrelationId() == null) {
            log.error("🔴 CorrelationId is NULL!");
            return;
        }

        switch (response.getType()) {

            case "single" -> {
                CommentResponseTo dto = objectMapper.convertValue(
                        response.getPayload(),
                        CommentResponseTo.class
                );

                holder.completeSingle(response.getCorrelationId(), dto);

                log.info("✅ Completed SINGLE future");
            }

            case "list" -> {
                List<CommentResponseTo> list = objectMapper.convertValue(
                        response.getPayload(),
                        new TypeReference<List<CommentResponseTo>>() {}
                );

                CommentListResponseTo wrapper = new CommentListResponseTo();
                wrapper.setComments(list);
                wrapper.setCorrelationId(response.getCorrelationId());

                holder.completeList(response.getCorrelationId(), wrapper);

                log.info("✅ Completed LIST future");
            }

            default -> log.error("🔴 Unknown response type: {}", response.getType());
        }
    }
}