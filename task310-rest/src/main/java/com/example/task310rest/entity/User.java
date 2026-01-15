package com.example.task310rest.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Сущность User (Пользователь)
 * Содержит данные о пользователе системы
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    
    /**
     * Уникальный идентификатор пользователя
     */
    private Long id;
    
    /**
     * Логин пользователя (email)
     * Диапазон: 2-64 символа
     */
    private String login;
    
    /**
     * Пароль пользователя
     * Диапазон: 8-128 символов
     */
    private String password;
    
    /**
     * Имя пользователя
     * Диапазон: 2-64 символа
     */
    private String firstname;
    
    /**
     * Фамилия пользователя
     * Диапазон: 2-64 символа
     */
    private String lastname;
    
    /**
     * Дата и время создания записи
     */
    private LocalDateTime createdAt;
    
    /**
     * Дата и время последнего обновления записи
     */
    private LocalDateTime updatedAt;
}
