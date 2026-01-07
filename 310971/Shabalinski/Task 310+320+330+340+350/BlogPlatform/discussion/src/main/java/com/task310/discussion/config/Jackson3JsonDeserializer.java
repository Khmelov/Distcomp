package com.task310.discussion.config;

import tools.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class Jackson3JsonDeserializer implements Deserializer<Object> {
    private static final Logger logger = LoggerFactory.getLogger(Jackson3JsonDeserializer.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        // No additional configuration needed
    }

    @Override
    public Object deserialize(String topic, byte[] data) {
        if (data == null) {
            return null;
        }
        try {
            // Deserialize as generic Object (Map)
            return objectMapper.readValue(data, Object.class);
        } catch (Exception e) {
            logger.error("Error deserializing JSON from bytes: {}", e.getMessage(), e);
            throw new RuntimeException("Error deserializing JSON from bytes", e);
        }
    }

    @Override
    public void close() {
        // No resources to close
    }
}

