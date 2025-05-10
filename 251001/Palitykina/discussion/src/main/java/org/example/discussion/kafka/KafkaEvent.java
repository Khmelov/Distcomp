package org.example.discussion.kafka;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.discussion.entity.Message;

@Data
@AllArgsConstructor
public class KafkaEvent {
    private CrudEnum eventType;
    private Message message;

}
