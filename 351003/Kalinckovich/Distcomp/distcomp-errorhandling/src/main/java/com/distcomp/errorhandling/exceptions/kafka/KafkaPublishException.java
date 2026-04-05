package com.distcomp.errorhandling.exceptions.kafka;

public class KafkaPublishException extends RuntimeException {
    public KafkaPublishException(final String message, final Throwable cause) {
        super(message, cause);
    }
}