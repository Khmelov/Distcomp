package com.example.entitiesapp.repositories;

import com.example.entitiesapp.entities.Sticker;
import java.util.List;

public interface StickerRepository extends CrudRepository<Sticker, Long> {
    List<Sticker> findByArticleId(Long articleId);
    List<Sticker> findByName(String name);
}