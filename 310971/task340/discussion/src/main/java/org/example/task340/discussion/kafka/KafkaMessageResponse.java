package org.example.task340.discussion.kafka;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

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
    private List<KafkaMessageResponse> messages; // For GET all
}

