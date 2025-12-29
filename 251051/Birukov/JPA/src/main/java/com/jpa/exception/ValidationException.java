package com.jpa.exception;

public class ValidationException extends RuntimeException {
	public ValidationException(String message) {
		super(message);
	}
}