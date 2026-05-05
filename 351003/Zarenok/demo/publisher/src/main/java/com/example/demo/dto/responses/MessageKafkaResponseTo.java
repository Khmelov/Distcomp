package com.example.demo.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MessageKafkaResponseTo {
    private Long id;
    private Long issueId;
    private String content;
    private String state;
}
