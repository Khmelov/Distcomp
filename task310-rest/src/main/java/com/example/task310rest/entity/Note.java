package com.example.task310rest.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Сущность Note (Заметка)
 * Представляет комментарий или заметку к твиту
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Note {
    
    /**
     * Уникальный идентификатор заметки
     */
    private Long id;
    
    /**
     * ID твита, к которому относится заметка
     * Foreign Key → Tweet.id
     */
    private Long tweetId;
    
    /**
     * Содержание заметки
     * Диапазон: 2-2048 символов
     */
    private String content;
    
    /**
     * Дата и время создания заметки
     */
    private LocalDateTime createdAt;
    
    /**
     * Дата и время последнего обновления заметки
     */
    private LocalDateTime updatedAt;
}
