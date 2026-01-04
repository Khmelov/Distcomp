package com.example.entitiesapp.repositories;

import com.example.entitiesapp.entities.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {
    List<Article> findByWriterId(Long writerId);

    @Query("SELECT a FROM Article a JOIN a.stickers s WHERE s.id = :stickerId")
    List<Article> findByStickerId(@Param("stickerId") Long stickerId);

    @Query("SELECT a FROM Article a JOIN a.stickers s WHERE s.name = :stickerName")
    List<Article> findByStickerName(@Param("stickerName") String stickerName);

    boolean existsByTitle(String title);
}