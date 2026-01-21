package com.example.publisher.dto;

public class KafkaReactionResponse {
    private String id;
    private String state; // APPROVE или DECLINE

    public KafkaReactionResponse() {}

    public KafkaReactionResponse(String id, String state) {
        this.id = id;
        this.state = state;
    }

    // Геттеры и сеттеры
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
}