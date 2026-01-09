package org.example.task340.publisher.kafka;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KafkaMessageResponse {
    private String requestId;
    private String operation;
    private Long id;
    private Long tweetId;
    private String content;
    private String country;
    private String state; // PENDING, APPROVE, DECLINE
    private String error;
    private java.util.List<KafkaMessageResponse> messages; // For GET all
}

