package com.example.distcomp.dto;

import com.example.distcomp.dto.MessageRequestTo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MessageRequestKafka {
    private MessageRequestTo request;
    private String requestType;
}
