package com.github.Lexya06.startrestapp.service.customexception;

import lombok.Getter;

import java.util.Collection;
import java.util.stream.Collectors;

public class MyEntitiesNotFoundException extends RuntimeException {
    @Getter
    final Collection<Long> ids;

    @Getter
    final Class<?> entityClass;

    @Getter
    final String message;
    public MyEntitiesNotFoundException(Collection<Long> ids, Class<?> entityClass) {
        this.message = String.format("Entities of type %s with ids [%s] not found", entityClass.getSimpleName(), ids.stream().map(String::valueOf).collect(Collectors.joining(", ")));
        this.ids = ids;
        this.entityClass = entityClass;
    }
}
