package com.example.demo.labrest.dto;

import lombok.*;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KafkaNoticeRequest implements Serializable {

    public enum Operation {
        CREATE, READ, UPDATE, DELETE, READ_ALL_BY_TOPIC
    }

    private String correlationId;

    private Operation operation;

    private Long id;
    private Long topicId;
    private String content;
    private String country;
}