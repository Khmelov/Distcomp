package com.github.Lexya06.startrestapp.publisher.impl.service.customexception;

import lombok.Getter;

import java.util.Locale;

public class MyEntityNotFoundException extends RuntimeException {
    @Getter
    final Long id;

    @Getter
    final Class<?> entityClass;

    @Getter
    final String message;

    public MyEntityNotFoundException(Long id, Class<?> entityClass) {
        super("Entity " + entityClass.getSimpleName().toLowerCase(Locale.ENGLISH) + " with id= " + id + " not found");
        this.id = id;
        this.entityClass = entityClass;
        this.message = getMessage();
    }
}
