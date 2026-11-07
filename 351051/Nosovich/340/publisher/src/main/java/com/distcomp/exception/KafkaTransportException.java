package com.distcomp.exception;

/**
 * Signals that the Kafka exchange could not be completed. The caller may then
 * use the REST path to the discussion module.
 */
public class KafkaTransportException extends RuntimeException {

    public KafkaTransportException(String message) {
        super(message);
    }

    public KafkaTransportException(String message, Throwable cause) {
        super(message, cause);
    }
}
