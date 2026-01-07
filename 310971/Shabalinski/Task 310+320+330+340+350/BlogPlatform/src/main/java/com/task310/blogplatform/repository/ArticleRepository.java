package com.task310.blogplatform.repository;

import com.task310.blogplatform.model.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ArticleRepository extends CrudRepository<Article, Long> {
    Page<Article> findAll(Pageable pageable);
    
    List<Article> findByUserId(Long userId);
    
    Page<Article> findByUserId(Long userId, Pageable pageable);
    
    Optional<Article> findByTitle(String title);
    
    @Query("SELECT a FROM Article a WHERE a.title LIKE %:title%")
    Page<Article> findByTitleContaining(@Param("title") String title, Pageable pageable);
    
    @Query("SELECT a FROM Article a WHERE a.content LIKE %:content%")
    Page<Article> findByContentContaining(@Param("content") String content, Pageable pageable);
    
    @Query("SELECT a FROM Article a JOIN a.user u WHERE u.login = :login")
    Page<Article> findByUserLogin(@Param("login") String login, Pageable pageable);
    
    @Query("SELECT a FROM Article a JOIN a.labels l WHERE l.name = :labelName")
    Page<Article> findByLabelName(@Param("labelName") String labelName, Pageable pageable);
    
    @Query("SELECT a FROM Article a JOIN a.labels l WHERE l.id IN :labelIds")
    Page<Article> findByLabelIds(@Param("labelIds") List<Long> labelIds, Pageable pageable);
}

