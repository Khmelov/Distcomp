package com.example.restapi.exception;

public class EntityNotFoundException extends RuntimeException {
    private final long id;

    public EntityNotFoundException(String entityName, long id) {
        super(entityName + " with id=" + id + " not found");
        this.id = id;
    }

    public long getId() {
        return id;
    }
}
