package com.sergey.orsik.exception;

public class EntityNotFoundException extends RuntimeException {

    private final String entityName;
    private final Long id;

    public EntityNotFoundException(String entityName, Long id) {
        super(entityName + " with id " + id + " not found");
        this.entityName = entityName;
        this.id = id;
    }

    public String getEntityName() {
        return entityName;
    }

    public Long getId() {
        return id;
    }
}
