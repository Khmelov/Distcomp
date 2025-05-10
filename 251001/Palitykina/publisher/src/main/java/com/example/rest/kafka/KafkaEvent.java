package com.example.rest.kafka;

import com.example.rest.entity.Message;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
public class KafkaEvent {
    private CrudEnum eventType;
    private Message message;

}

