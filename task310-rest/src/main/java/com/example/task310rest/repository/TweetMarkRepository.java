package com.example.task310rest.repository;

import com.example.task310rest.entity.TweetMark;

import java.util.List;

/**
 * Репозиторий для связи многие-ко-многим между Tweet и Mark
 */
public interface TweetMarkRepository {
    
    /**
     * Добавить связь между твитом и меткой
     * @param tweetId ID твита
     * @param markId ID метки
     */
    void addTweetMark(Long tweetId, Long markId);
    
    /**
     * Удалить связь между твитом и меткой
     * @param tweetId ID твита
     * @param markId ID метки
     * @return true если связь была удалена, false если не найдена
     */
    boolean removeTweetMark(Long tweetId, Long markId);
    
    /**
     * Получить все метки для твита
     * @param tweetId ID твита
     * @return список ID меток
     */
    List<Long> getMarkIdsByTweetId(Long tweetId);
    
    /**
     * Получить все твиты для метки
     * @param markId ID метки
     * @return список ID твитов
     */
    List<Long> getTweetIdsByMarkId(Long markId);
    
    /**
     * Получить все твиты для метки по имени
     * @param markName имя метки
     * @return список ID твитов
     */
    List<Long> getTweetIdsByMarkName(String markName);
    
    /**
     * Удалить все связи для твита
     * @param tweetId ID твита
     */
    void removeAllByTweetId(Long tweetId);
    
    /**
     * Удалить все связи для метки
     * @param markId ID метки
     */
    void removeAllByMarkId(Long markId);
}
