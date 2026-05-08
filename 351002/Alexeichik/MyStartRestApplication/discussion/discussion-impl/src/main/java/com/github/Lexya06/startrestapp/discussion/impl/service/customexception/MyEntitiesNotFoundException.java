package com.github.Lexya06.startrestapp.discussion.impl.service.customexception;

import lombok.Getter;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.Value;

import java.io.Serializable;
import java.util.Collection;
import java.util.stream.Collectors;

public class MyEntitiesNotFoundException extends RuntimeException {
    @Getter
    final Collection<String> keys;

    @Getter
    final Class<?> entityClass;

    @Getter
    final String message;
    public MyEntitiesNotFoundException(Collection<String> keys, Class<?> entityClass) {
        this.message = String.format("Entities of type %s with keys [%s] not found", entityClass.getSimpleName(), keys.stream().map(String::valueOf).collect(Collectors.joining(", ")));
        this.keys = keys;
        this.entityClass = entityClass;
    }
}
