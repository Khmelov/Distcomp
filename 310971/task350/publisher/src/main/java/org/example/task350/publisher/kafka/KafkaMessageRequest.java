package org.example.task350.publisher.kafka;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KafkaMessageRequest {
    private String operation; // "POST", "GET", "PUT", "DELETE"
    private Long id;
    private Long tweetId;
    private String content;
    private String country;
    private String requestId; // For correlating request/response
}

