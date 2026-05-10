package com.lizaveta.notebook.kafka;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;

@JsonIgnoreProperties(ignoreUnknown = true)
public record NoticeKafkaOutEnvelope(
        String correlationId,
        boolean success,
        Integer errorCode,
        String errorMessage,
        JsonNode payload) {
}
