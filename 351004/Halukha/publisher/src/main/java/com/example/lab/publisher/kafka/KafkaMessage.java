package com.example.lab.publisher.kafka;

import java.time.LocalDateTime;

public class KafkaMessage<T> {
    private String eventType;  // CREATE, UPDATE, DELETE
    private String entityType; // User, News, Marker
    private T data;
    private LocalDateTime timestamp;
    private String operationId;
    
    public KafkaMessage() {}
    
    public KafkaMessage(String eventType, String entityType, T data) {
        this.eventType = eventType;
        this.entityType = entityType;
        this.data = data;
        this.timestamp = LocalDateTime.now();
        this.operationId = java.util.UUID.randomUUID().toString();
    }
    
    // Getters and Setters
    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }
    
    public String getEntityType() { return entityType; }
    public void setEntityType(String entityType) { this.entityType = entityType; }
    
    public T getData() { return data; }
    public void setData(T data) { this.data = data; }
    
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    
    public String getOperationId() { return operationId; }
    public void setOperationId(String operationId) { this.operationId = operationId; }
}