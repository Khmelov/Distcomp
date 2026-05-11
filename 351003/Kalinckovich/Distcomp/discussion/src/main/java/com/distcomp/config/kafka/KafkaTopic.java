package com.distcomp.config.kafka;

import lombok.AllArgsConstructor;
import lombok.Getter;



@Getter
@AllArgsConstructor
public enum KafkaTopic {

    IN_TOPIC("InTopic", "notes-inbound"),
    OUT_TOPIC("OutTopic", "notes-outbound");

    private final String name;
    private final String group;
    
}
