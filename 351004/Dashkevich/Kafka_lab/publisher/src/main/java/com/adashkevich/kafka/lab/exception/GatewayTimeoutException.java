package com.adashkevich.kafka.lab.exception;

public class GatewayTimeoutException extends ApiException {
    public GatewayTimeoutException(String message, String errorCode) {
        super(message, 504, errorCode);
    }
}
