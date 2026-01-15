package com.example.task310rest.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Сущность Mark (Метка/Тег)
 * Представляет тег, который можно присвоить твиту
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Mark {
    
    /**
     * Уникальный идентификатор метки
     */
    private Long id;
    
    /**
     * Название метки
     * Диапазон: 2-32 символа
     */
    private String name;
    
    /**
     * Дата и время создания метки
     */
    private LocalDateTime createdAt;
    
    /**
     * Дата и время последнего обновления метки
     */
    private LocalDateTime updatedAt;
}
