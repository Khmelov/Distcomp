package com.example.task320jpa.repository;

import com.example.task320jpa.entity.Tweet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * JPA Repository для сущности Tweet
 */
@Repository
public interface TweetRepository extends JpaRepository<Tweet, Long> {
    
    /**
     * Найти все твиты пользователя
     */
    List<Tweet> findByUserId(Long userId);
    
    /**
     * Найти все твиты пользователя с пагинацией
     */
    Page<Tweet> findByUserId(Long userId, Pageable pageable);
    
    /**
     * Найти твиты по заголовку (частичное совпадение)
     */
    Page<Tweet> findByTitleContainingIgnoreCase(String title, Pageable pageable);
    
    /**
     * Найти твиты по содержимому (частичное совпадение)
     */
    Page<Tweet> findByContentContainingIgnoreCase(String content, Pageable pageable);
    
    /**
     * Найти твиты по метке
     */
    @Query("SELECT t FROM Tweet t JOIN t.marks m WHERE m.id = :markId")
    Page<Tweet> findByMarkId(@Param("markId") Long markId, Pageable pageable);
    
    /**
     * Найти твиты по имени метки
     */
    @Query("SELECT t FROM Tweet t JOIN t.marks m WHERE m.name = :markName")
    Page<Tweet> findByMarkName(@Param("markName") String markName, Pageable pageable);
    
    /**
     * Найти все твиты с пагинацией
     */
    Page<Tweet> findAll(Pageable pageable);
}
