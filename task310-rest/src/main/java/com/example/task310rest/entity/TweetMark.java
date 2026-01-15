package com.example.task310rest.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Сущность TweetMark
 * Связующая таблица для отношения многие-ко-многим между Tweet и Mark
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TweetMark {
    
    /**
     * ID твита
     */
    private Long tweetId;
    
    /**
     * ID метки
     */
    private Long markId;
}
