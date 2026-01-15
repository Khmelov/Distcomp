package com.example.task310rest.exception;

/**
 * Исключение, выбрасываемое когда запрашиваемый ресурс не найден
 * Приводит к HTTP статусу 404 Not Found
 */
public class ResourceNotFoundException extends RuntimeException {
    
    public ResourceNotFoundException(String message) {
        super(message);
    }
    
    public ResourceNotFoundException(String resourceName, Long id) {
        super(String.format("%s with id=%d not found", resourceName, id));
    }
}
