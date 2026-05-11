package com.github.Lexya06.startrestapp.discussion.impl.service.customexception;

import lombok.Getter;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.Value;

import java.io.Serializable;
import java.util.Locale;

public class MyEntityNotFoundException extends RuntimeException {
    @Getter
    final String key;

    @Getter
    final Class<?> entityClass;

    @Getter
    final String message;

    public MyEntityNotFoundException(String key, Class<?> entityClass) {
        this.key = key;
        this.entityClass = entityClass;
        this.message = "Entity " + entityClass.getSimpleName().toLowerCase(Locale.ENGLISH) + " with key= " + key + " not found";
    }
}
