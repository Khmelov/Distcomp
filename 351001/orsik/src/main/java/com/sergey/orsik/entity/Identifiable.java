package com.sergey.orsik.entity;

/**
 * Общая абстракция для сущностей с идентификатором.
 */
public interface Identifiable {

    Long getId();

    void setId(Long id);
}
