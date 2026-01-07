package com.task310.blogplatform.config;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.type.TypeFactory;
import tools.jackson.core.type.TypeReference;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class Jackson3JsonRedisSerializer implements RedisSerializer<Object> {
    private static final Logger logger = LoggerFactory.getLogger(Jackson3JsonRedisSerializer.class);
    private final ObjectMapper objectMapper;
    private static final byte[] EMPTY_ARRAY = new byte[0];
    private static final String TYPE_KEY = "@class";

    public Jackson3JsonRedisSerializer() {
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public byte[] serialize(Object obj) throws SerializationException {
        if (obj == null) {
            return EMPTY_ARRAY;
        }
        try {
            // Serialize object with type information
            Map<String, Object> wrapper = new java.util.HashMap<>();
            wrapper.put(TYPE_KEY, obj.getClass().getName());
            wrapper.put("value", obj);
            return objectMapper.writeValueAsBytes(wrapper);
        } catch (Exception e) {
            logger.error("Error serializing object to JSON: {}", e.getMessage(), e);
            throw new SerializationException("Error serializing object to JSON", e);
        }
    }

    @Override
    public Object deserialize(byte[] bytes) throws SerializationException {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        try {
            // Deserialize wrapper with type information
            Map<String, Object> wrapper = objectMapper.readValue(bytes, 
                new TypeReference<Map<String, Object>>() {});
            
            if (wrapper == null || !wrapper.containsKey(TYPE_KEY)) {
                // Fallback: try to deserialize as plain object (for backward compatibility)
                return objectMapper.readValue(bytes, Object.class);
            }
            
            String className = (String) wrapper.get(TYPE_KEY);
            Object value = wrapper.get("value");
            
            if (className != null && value != null) {
                // Convert value to the correct type
                Class<?> clazz = Class.forName(className);
                return objectMapper.convertValue(value, clazz);
            }
            
            return value;
        } catch (ClassNotFoundException e) {
            logger.warn("Class not found during deserialization, returning as Map: {}", e.getMessage());
            // Fallback: return as Map
            try {
                return objectMapper.readValue(bytes, Object.class);
            } catch (Exception ex) {
                logger.error("Error deserializing JSON from bytes: {}", ex.getMessage(), ex);
                throw new SerializationException("Error deserializing JSON from bytes", ex);
            }
        } catch (Exception e) {
            logger.error("Error deserializing JSON from bytes: {}", e.getMessage(), e);
            throw new SerializationException("Error deserializing JSON from bytes", e);
        }
    }
}

