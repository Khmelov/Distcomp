package com.task310.discussion.config;

import tools.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class Jackson3JsonSerializer implements Serializer<Object> {
    private static final Logger logger = LoggerFactory.getLogger(Jackson3JsonSerializer.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        // No additional configuration needed
    }

    @Override
    public byte[] serialize(String topic, Object data) {
        if (data == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsBytes(data);
        } catch (Exception e) {
            logger.error("Error serializing object to JSON: {}", e.getMessage(), e);
            throw new RuntimeException("Error serializing object to JSON", e);
        }
    }

    @Override
    public void close() {
        // No resources to close
    }
}

