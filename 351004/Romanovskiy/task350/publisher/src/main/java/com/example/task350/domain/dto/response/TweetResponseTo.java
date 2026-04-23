package com.example.task350.domain.dto.response;
import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.List; // ВАЖНО: добавить импорт

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TweetResponseTo implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    private Long authorId;
    private String title;
    private String content;
    private LocalDateTime created;
    private LocalDateTime modified;

    // ДОБАВЬ ЭТУ СТРОКУ:
    private List<MarkerResponseTo> markers;
}