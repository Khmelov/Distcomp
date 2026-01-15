package com.example.task310rest.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Сущность Tweet (Твит)
 * Представляет публикацию пользователя
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Tweet {
    
    /**
     * Уникальный идентификатор твита
     */
    private Long id;
    
    /**
     * ID пользователя, который создал твит
     * Foreign Key → User.id
     */
    private Long userId;
    
    /**
     * Заголовок твита
     * Диапазон: 2-64 символа
     */
    private String title;
    
    /**
     * Содержание твита
     * Диапазон: 4-2048 символов
     */
    private String content;
    
    /**
     * Дата и время создания твита
     */
    private LocalDateTime createdAt;
    
    /**
     * Дата и время последнего обновления твита
     */
    private LocalDateTime updatedAt;
}
