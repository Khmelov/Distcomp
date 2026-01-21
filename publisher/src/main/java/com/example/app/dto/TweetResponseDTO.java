package com.example.app.dto;

import java.time.Instant;
import java.util.List;

public record TweetResponseDTO(
        Long id,
        Long authorId,
        String title,
        String content,
        Instant created,
        Instant modified,
        List<ReactionResponseDTO> reactions  // Добавляем реакции
) {
    // Конструктор без реакций (для обратной совместимости)
    public TweetResponseDTO(Long id, Long authorId, String title, String content, 
                           Instant created, Instant modified) {
        this(id, authorId, title, content, created, modified, List.of());
    }
    
    // Метод для создания новой версии с реакциями
    public TweetResponseDTO withReactions(List<ReactionResponseDTO> reactions) {
        return new TweetResponseDTO(
            this.id,
            this.authorId,
            this.title,
            this.content,
            this.created,
            this.modified,
            reactions
        );
    }
}