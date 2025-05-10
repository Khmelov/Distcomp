package com.example.discussion.dto;

import com.example.discussion.dto.MessageResponseTo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MessageResponseKafka {
    private MessageResponseTo request;
    private String responseType;
    private String responseStatus;
}
