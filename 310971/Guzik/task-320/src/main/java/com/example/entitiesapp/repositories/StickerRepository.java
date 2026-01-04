package com.example.entitiesapp.repositories;

import com.example.entitiesapp.entities.Sticker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface StickerRepository extends JpaRepository<Sticker, Long> {
    Optional<Sticker> findByName(String name);
    boolean existsByName(String name);

    @Query("SELECT s FROM Sticker s JOIN s.articles a WHERE a.id = :articleId")
    List<Sticker> findByArticleId(@Param("articleId") Long articleId);
}