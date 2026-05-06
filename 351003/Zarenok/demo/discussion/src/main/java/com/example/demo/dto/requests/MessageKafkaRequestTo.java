package com.example.demo.dto.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MessageKafkaRequestTo {
    private Long id;
    private Long issueId;
    private String content;
}
