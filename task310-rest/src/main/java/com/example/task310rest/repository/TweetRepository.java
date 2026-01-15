package com.example.task310rest.repository;

import com.example.task310rest.entity.Tweet;

import java.util.List;

/**
 * Репозиторий для сущности Tweet
 * Расширяет базовый CrudRepository дополнительными методами поиска
 */
public interface TweetRepository extends CrudRepository<Tweet, Long> {
    
    /**
     * Найти все твиты пользователя
     * @param userId ID пользователя
     * @return список твитов
     */
    List<Tweet> findByUserId(Long userId);
    
    /**
     * Найти все твиты по заголовку (частичное совпадение)
     * @param title заголовок
     * @return список твитов
     */
    List<Tweet> findByTitleContaining(String title);
    
    /**
     * Найти все твиты по содержимому (частичное совпадение)
     * @param content содержимое
     * @return список твитов
     */
    List<Tweet> findByContentContaining(String content);
}
