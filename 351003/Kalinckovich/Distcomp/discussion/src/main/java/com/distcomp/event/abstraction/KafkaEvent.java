package com.distcomp.event.abstraction;

import java.io.Serializable;
import java.time.Instant;

public interface KafkaEvent extends Serializable {
    String getEventId();
    String getEventType();
    Instant getTimestamp();
}
