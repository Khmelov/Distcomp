package com.lizaveta.notebook.kafka;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;

@JsonIgnoreProperties(ignoreUnknown = true)
public record NoticeKafkaInEnvelope(
        NoticeInMessageType type,
        String correlationId,
        JsonNode payload) {
}
