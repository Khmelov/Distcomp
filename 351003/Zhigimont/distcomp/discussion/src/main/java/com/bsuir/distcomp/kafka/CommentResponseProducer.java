package com.bsuir.distcomp.kafka;

import com.bsuir.distcomp.dto.CommentListResponseTo;
import com.bsuir.distcomp.dto.CommentResponseTo;
import com.bsuir.distcomp.dto.KafkaResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentResponseProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void send(CommentResponseTo response) {
        KafkaResponse kafkaResponse = new KafkaResponse();
        kafkaResponse.setCorrelationId(response.getCorrelationId());
        kafkaResponse.setType("single");
        kafkaResponse.setPayload(response);
        kafkaResponse.setStatus(
                response.getStatus() != null ? response.getStatus().name() : "OK"
        );

        log.info("🟤 Sending SINGLE via KafkaResponse: correlationId={}", response.getCorrelationId());

        kafkaTemplate.send("OutTopic", response.getCorrelationId(), kafkaResponse);
    }

    public void sendList(CommentListResponseTo response) {
        KafkaResponse kafkaResponse = new KafkaResponse();
        kafkaResponse.setCorrelationId(response.getCorrelationId());
        kafkaResponse.setType("list");
        kafkaResponse.setPayload(response.getComments());
        kafkaResponse.setStatus("OK");

        log.info("🟤 Sending LIST via KafkaResponse: correlationId={}, size={}",
                response.getCorrelationId(),
                response.getComments() != null ? response.getComments().size() : 0);

        kafkaTemplate.send("OutTopic", response.getCorrelationId(), kafkaResponse);
    }
}