// ApiError.java
package com.example.publisher.exception;

public record ApiError(String errorMessage, int errorCode) {}